package io.hychou.runnable.python.runner.profile.service.impl;

import io.hychou.common.exception.service.ServiceException;
import io.hychou.common.exception.service.clienterror.ElementNotExistException;
import io.hychou.common.exception.service.clienterror.NullParameterException;
import io.hychou.file.dao.FileEntityRepository;
import io.hychou.file.entity.FileEntity;
import io.hychou.runnable.python.anacondayaml.dao.AnacondaYamlRepository;
import io.hychou.runnable.python.runner.profile.dao.PythonRunnerProfileRepository;
import io.hychou.runnable.python.runner.profile.entity.PythonRunnerProfileEntity;
import io.hychou.runnable.python.runner.profile.entity.PythonRunnerProfileInfo;
import io.hychou.runnable.python.runner.profile.service.PythonRunnerProfileService;
import io.hychou.runnable.python.runner.service.PythonRunnerService;
import io.hychou.runnable.timedependent.entity.TimeDependentEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PythonRunnerProfileServiceImpl implements PythonRunnerProfileService {
    private static final String ID_STRING = "Id";
    private final PythonRunnerProfileRepository pythonRunnerProfileRepository;
    private final FileEntityRepository fileEntityRepository;
    private final AnacondaYamlRepository anacondaYamlRepository;
    private final PythonRunnerService pythonRunner;

    @Value("${workdirectory}")
    private String runnableBaseDirectory;

    public PythonRunnerProfileServiceImpl(PythonRunnerProfileRepository pythonRunnerProfileRepository,
                                          FileEntityRepository fileEntityRepository,
                                          AnacondaYamlRepository anacondaYamlRepository,
                                          PythonRunnerService pythonRunner) {
        this.pythonRunnerProfileRepository = pythonRunnerProfileRepository;
        this.fileEntityRepository = fileEntityRepository;
        this.anacondaYamlRepository = anacondaYamlRepository;
        this.pythonRunner = pythonRunner;
    }

    @Override
    public List<PythonRunnerProfileInfo> listPythonRunnerProfileInfo() {
        return pythonRunnerProfileRepository.findPythonRunnerProfileInfoBy();
    }

    @Override
    public PythonRunnerProfileInfo readPythonRunnerProfileInfoById(Long id) throws ServiceException {
        if (id == null) {
            throw new NullParameterException(new PythonRunnerProfileEntity().getStringQueryWithNullParam(ID_STRING));
        }
        Optional<PythonRunnerProfileInfo> pythonRunnerProfileInfo = pythonRunnerProfileRepository.findPythonRunnerProfileInfoById(id);
        if (pythonRunnerProfileInfo.isPresent()) {
            return pythonRunnerProfileInfo.get();
        } else {
            throw new ElementNotExistException(new PythonRunnerProfileEntity().getStringNotExistForParam(ID_STRING, id));
        }
    }

    @Override
    public PythonRunnerProfileEntity readPythonRunnerProfileById(Long id) throws ServiceException {
        if (id == null) {
            throw new NullParameterException(new PythonRunnerProfileEntity().getStringQueryWithNullParam(ID_STRING));
        }
        Optional<PythonRunnerProfileEntity> pythonRunnerProfileEntity = pythonRunnerProfileRepository.findById(id);
        if (pythonRunnerProfileEntity.isPresent()) {
            return pythonRunnerProfileEntity.get();
        } else {
            throw new ElementNotExistException(new PythonRunnerProfileEntity().getStringNotExistForParam(ID_STRING, id));
        }
    }

    @Override
    public PythonRunnerProfileEntity createPythonRunnerProfile(PythonRunnerProfileEntity pythonRunnerProfileEntity) throws ServiceException {
        if (pythonRunnerProfileEntity == null || pythonRunnerProfileEntity.getPythonCode() == null
                || pythonRunnerProfileEntity.getDependencies() == null || pythonRunnerProfileEntity.getEnvironment() == null) {
            throw new NullParameterException(new PythonRunnerProfileEntity().getStringCreateNull());
        }
        // if a same pythonRunnerProfileEntity exist and be run successfully, then do not run it again
        Optional<PythonRunnerProfileEntity> theSameEntity = getTheSamePythonRunnerProfileEntity(pythonRunnerProfileEntity);
        if (theSameEntity.isPresent() && theSameEntity.get().isFinished()) {
            return theSameEntity.get();
        }
        pythonRunnerProfileEntity = pythonRunnerProfileRepository.save(pythonRunnerProfileEntity);
        pythonRunner.run(pythonRunnerProfileEntity, pythonRunnerProfileRepository);
        return pythonRunnerProfileEntity;
    }

    @Override
    public void deletePythonRunnerProfileById(Long id) throws ServiceException {
        if (id == null) {
            throw new NullParameterException(new PythonRunnerProfileEntity().getStringDeleteWithNullParam(ID_STRING));
        }
        if (!pythonRunnerProfileRepository.existsById(id)) {
            throw new ElementNotExistException(new PythonRunnerProfileEntity().getStringNotExistForParam(ID_STRING, id));
        }
        pythonRunnerProfileRepository.deleteById(id);
    }

    private Optional<PythonRunnerProfileEntity> getTheSamePythonRunnerProfileEntity(PythonRunnerProfileEntity pythonRunnerProfileEntity) throws ServiceException {
        if (!fileEntityRepository.existsById(pythonRunnerProfileEntity.getPythonCode().getTimeVariantData().getId())) {
            return Optional.empty();
        }
        if (!anacondaYamlRepository.existsById(pythonRunnerProfileEntity.getEnvironment().getTimeVariantData().getId())) {
            return Optional.empty();
        }
        for (TimeDependentEntity<FileEntity> timeDependentFileEntity : pythonRunnerProfileEntity.getDependencies()) {
            if (!fileEntityRepository.existsById(timeDependentFileEntity.getTimeVariantData().getId())) {
                return Optional.empty();
            }
        }

        // in default CrudRepository, a set is null if it is empty
        // and anEmptySet.equals(null) return false
        // To have a desired behavior, we query null if the set is empty
        Set<FileEntity> queryDependencies = pythonRunnerProfileEntity.getDependencies().stream().map(TimeDependentEntity::getTimeVariantData).collect(Collectors.toSet());
        if (queryDependencies.isEmpty()) {
            queryDependencies = null;
        }
        List<PythonRunnerProfileEntity> theSamePythonRunnerProfileEntities =
                pythonRunnerProfileRepository.findByPythonCode_TimeVariantDataAndEnvironment_TimeVariantDataAndDependencies_TimeVariantData(
                        pythonRunnerProfileEntity.getPythonCode().getTimeVariantData(),
                        pythonRunnerProfileEntity.getEnvironment().getTimeVariantData(),
                        queryDependencies);

        for (PythonRunnerProfileEntity theSamePythonRunnerProfileEntity : theSamePythonRunnerProfileEntities) {

            // following if statements check whether the used files/env is the same as before
            // if not, delete the old python runner and continue check the next one
            if (!theSamePythonRunnerProfileEntity.getPythonCode().isDependencyValid()) {
                this.deletePythonRunnerProfileById(theSamePythonRunnerProfileEntity.getId());
                continue;
            }
            if (!theSamePythonRunnerProfileEntity.getEnvironment().isDependencyValid()) {
                this.deletePythonRunnerProfileById(theSamePythonRunnerProfileEntity.getId());
                continue;
            }
            // the map-reduce return true if the dependencies is empty or all of the elements have good timestamp
            // use orElse(true) as we have done set comparison earlier, two empty sets are considered equal
            if (!theSamePythonRunnerProfileEntity.getDependencies().stream()
                    .map(TimeDependentEntity::isDependencyValid)
                    .reduce(Boolean::logicalAnd).orElse(true)) {
                this.deletePythonRunnerProfileById(theSamePythonRunnerProfileEntity.getId());
                continue;
            }
            return Optional.of(theSamePythonRunnerProfileEntity);
        }
        return Optional.empty();
    }
}
