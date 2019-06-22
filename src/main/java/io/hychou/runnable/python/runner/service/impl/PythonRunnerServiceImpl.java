package io.hychou.runnable.python.runner.service.impl;

import io.hychou.common.exception.service.ServiceException;
import io.hychou.common.exception.service.clienterror.ElementNotExistException;
import io.hychou.common.exception.service.clienterror.NullParameterException;
import io.hychou.file.dao.FileEntityRepository;
import io.hychou.runnable.TimeDependentEntity;
import io.hychou.runnable.python.anacondayaml.dao.AnacondaYamlRepository;
import io.hychou.runnable.python.runner.dao.PythonRunnerRepository;
import io.hychou.runnable.python.runner.entity.PythonRunnerEntity;
import io.hychou.runnable.python.runner.entity.PythonRunnerInfo;
import io.hychou.runnable.python.runner.entity.TimeDependentFileEntity;
import io.hychou.runnable.python.runner.service.PythonRunnerService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
public class PythonRunnerServiceImpl implements PythonRunnerService {
    private static final String ID_STRING = "Id";
    private final PythonRunnerRepository pythonRunnerRepository;
    private final FileEntityRepository fileEntityRepository;
    private final AnacondaYamlRepository anacondaYamlRepository;
    private final TaskExecutor taskExecutor;

    @Value("${workdirectory}")
    private String runnableBaseDirectory;

    public PythonRunnerServiceImpl(PythonRunnerRepository pythonRunnerRepository,
                                   FileEntityRepository fileEntityRepository,
                                   AnacondaYamlRepository anacondaYamlRepository,
                                   TaskExecutor taskExecutor) {
        this.pythonRunnerRepository = pythonRunnerRepository;
        this.fileEntityRepository = fileEntityRepository;
        this.anacondaYamlRepository = anacondaYamlRepository;
        this.taskExecutor = taskExecutor;
    }

    @Override
    public List<PythonRunnerInfo> listPythonRunnerInfo() {
        return pythonRunnerRepository.findPythonRunnerInfoBy();
    }

    @Override
    public PythonRunnerInfo readPythonRunnerInfoById(Long id) throws ServiceException {
        if (id == null) {
            throw new NullParameterException(new PythonRunnerEntity().getStringQueryWithNullParam(ID_STRING));
        }
        Optional<PythonRunnerInfo> pythonRunnerInfo = pythonRunnerRepository.findPythonRunnerInfoById(id);
        if (pythonRunnerInfo.isPresent()) {
            return pythonRunnerInfo.get();
        } else {
            throw new ElementNotExistException(new PythonRunnerEntity().getStringNotExistForParam(ID_STRING, id));
        }
    }

    @Override
    public PythonRunnerEntity readPythonRunnerById(Long id) throws ServiceException {
        if (id == null) {
            throw new NullParameterException(new PythonRunnerEntity().getStringQueryWithNullParam(ID_STRING));
        }
        Optional<PythonRunnerEntity> pythonRunnerEntity = pythonRunnerRepository.findById(id);
        if (pythonRunnerEntity.isPresent()) {
            return pythonRunnerEntity.get();
        } else {
            throw new ElementNotExistException(new PythonRunnerEntity().getStringNotExistForParam(ID_STRING, id));
        }
    }

    @Override
    public PythonRunnerEntity createPythonRunner(PythonRunnerEntity pythonRunnerEntity) throws ServiceException {
        if (pythonRunnerEntity == null || pythonRunnerEntity.getPythonCode() == null
                || pythonRunnerEntity.getDependencies() == null || pythonRunnerEntity.getEnvironment() == null) {
            throw new NullParameterException(new PythonRunnerEntity().getStringCreateNull());
        }
        // if a same pythonRunnerEntity exist, do not run it again
        Optional<PythonRunnerEntity> theSameEntity = getTheSamePythonRunnerEntity(pythonRunnerEntity);
        if (theSameEntity.isPresent()) {
            return theSameEntity.get();
        }
        pythonRunnerEntity = pythonRunnerRepository.save(pythonRunnerEntity);
        taskExecutor.execute(new PythonRunnable(pythonRunnerEntity));
        return pythonRunnerEntity;
    }

    @Override
    public void deletePythonRunnerById(Long id) throws ServiceException {
        if (id == null) {
            throw new NullParameterException(new PythonRunnerEntity().getStringDeleteWithNullParam(ID_STRING));
        }
        if (!pythonRunnerRepository.existsById(id)) {
            throw new ElementNotExistException(new PythonRunnerEntity().getStringNotExistForParam(ID_STRING, id));
        }
        pythonRunnerRepository.deleteById(id);
    }

    private Optional<PythonRunnerEntity> getTheSamePythonRunnerEntity(PythonRunnerEntity pythonRunnerEntity) throws ServiceException {
        if (!fileEntityRepository.existsByName(pythonRunnerEntity.getPythonCode().getCrudTimeVariantDataStructure().getName())) {
            return Optional.empty();
        }
        if (!anacondaYamlRepository.existsByName(pythonRunnerEntity.getEnvironment().getCrudTimeVariantDataStructure().getName())) {
            return Optional.empty();
        }
        for (TimeDependentFileEntity timeDependentFileEntity : pythonRunnerEntity.getDependencies()) {
            if (!fileEntityRepository.existsByName(timeDependentFileEntity.getCrudTimeVariantDataStructure().getName())) {
                return Optional.empty();
            }
        }

        // CrudRepository seems don't have good support for selecting by set/list comparison
        // So we select by some composite columns and then do the set/list comparison manually
        List<PythonRunnerEntity> theSamePythonRunnerEntities =
                pythonRunnerRepository.findByPythonCode_CrudTimeVariantDataStructureAndEnvironment_CrudTimeVariantDataStructure(
                        pythonRunnerEntity.getPythonCode().getCrudTimeVariantDataStructure(),
                        pythonRunnerEntity.getEnvironment().getCrudTimeVariantDataStructure());
        for (PythonRunnerEntity theSamePythonRunnerEntity : theSamePythonRunnerEntities) {
            if (theSamePythonRunnerEntity.getDependencies().size() != pythonRunnerEntity.getDependencies().size() ||
                    !theSamePythonRunnerEntity.getDependencies().containsAll(pythonRunnerEntity.getDependencies()) ||
                    !pythonRunnerEntity.getDependencies().containsAll(theSamePythonRunnerEntity.getDependencies())) {
                continue;
            }

            // following if statements check whether the used files/env is the same as before
            // if not, delete the old python runner and continue check the next one
            if (!theSamePythonRunnerEntity.getPythonCode().isDenpendencyValid()) {
                this.deletePythonRunnerById(theSamePythonRunnerEntity.getId());
                continue;
            }
            if (!theSamePythonRunnerEntity.getEnvironment().isDenpendencyValid()) {
                this.deletePythonRunnerById(theSamePythonRunnerEntity.getId());
                continue;
            }
            // the map-reduce return true if the dependencies is empty or all of the elements have good timestamp
            // use orElse(true) as we have done set comparison earlier, two empty sets are considered equal
            if (!theSamePythonRunnerEntity.getDependencies().stream()
                    .map(TimeDependentEntity::isDenpendencyValid)
                    .reduce(Boolean::logicalAnd).orElse(true)) {
                this.deletePythonRunnerById(theSamePythonRunnerEntity.getId());
                continue;
            }
            return Optional.of(theSamePythonRunnerEntity);
        }
        return Optional.empty();
    }

    private class PythonRunnable implements Runnable {

        private PythonRunnerEntity pythonRunnerEntity;
        private Path absoluteWorkDirectory;

        private PythonRunnable(PythonRunnerEntity pythonRunnerEntity) {
            this.pythonRunnerEntity = pythonRunnerEntity;
        }

        @Override
        public void run() {
            try {
                pythonRunnerEntity.toPreparingState();
                pythonRunnerRepository.save(pythonRunnerEntity);
                prepareEnvironment();
                pythonRunnerEntity.toRunningState();
                pythonRunnerRepository.save(pythonRunnerEntity);
                runPythonCode();
                pythonRunnerEntity.toCleaningState();
                pythonRunnerRepository.save(pythonRunnerEntity);
                cleanEnvironment();
            } catch (IOException | InterruptedException e) {
                pythonRunnerEntity.addErrorMessage(e.getMessage());
            } finally {
                pythonRunnerEntity.toFinishedState();
                pythonRunnerRepository.save(pythonRunnerEntity);
            }
        }

        private void prepareEnvironment() throws IOException {
            this.absoluteWorkDirectory = Files.createTempDirectory(null);
            this.absoluteWorkDirectory.toFile().deleteOnExit();
            pythonRunnerEntity.getPythonCode().getCrudTimeVariantDataStructure().writeToFile(this.absoluteWorkDirectory);
            pythonRunnerEntity.getEnvironment().getCrudTimeVariantDataStructure().prepareEnvironment(this.absoluteWorkDirectory);
            for (TimeDependentFileEntity dependency : pythonRunnerEntity.getDependencies()) {
                dependency.getCrudTimeVariantDataStructure().writeToFile(this.absoluteWorkDirectory);
            }
        }

        private void runPythonCode() throws InterruptedException {
            Thread.sleep(5000);
        }

        private void cleanEnvironment() throws IOException {
            FileUtils.deleteDirectory(this.absoluteWorkDirectory.toFile());
        }
    }
}
