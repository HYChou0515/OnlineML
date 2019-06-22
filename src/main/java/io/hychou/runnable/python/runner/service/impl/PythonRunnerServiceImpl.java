package io.hychou.runnable.python.runner.service.impl;

import io.hychou.common.exception.service.ServiceException;
import io.hychou.common.exception.service.clienterror.ElementNotExistException;
import io.hychou.common.exception.service.clienterror.NullParameterException;
import io.hychou.file.dao.FileEntityRepository;
import io.hychou.file.entity.FileEntity;
import io.hychou.runnable.timedependent.entity.TimeDependentEntity;
import io.hychou.runnable.python.anacondayaml.dao.AnacondaYamlRepository;
import io.hychou.runnable.python.runner.dao.PythonRunnerRepository;
import io.hychou.runnable.python.runner.entity.PythonRunnerEntity;
import io.hychou.runnable.python.runner.entity.PythonRunnerInfo;
import io.hychou.runnable.python.runner.service.PythonRunnerService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

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
        if (!fileEntityRepository.existsById(pythonRunnerEntity.getPythonCode().getTimeVariantData().getId())) {
            return Optional.empty();
        }
        if (!anacondaYamlRepository.existsById(pythonRunnerEntity.getEnvironment().getTimeVariantData().getId())) {
            return Optional.empty();
        }
        for (TimeDependentEntity<FileEntity> timeDependentFileEntity : pythonRunnerEntity.getDependencies()) {
            if (!fileEntityRepository.existsById(timeDependentFileEntity.getTimeVariantData().getId())) {
                return Optional.empty();
            }
        }

        // in default CrudRepository, a set is null if it is empty
        // and anEmptySet.equals(null) return false
        // To have a desired behavior, we query null if the set is empty
        Set<FileEntity> queryDependencies = pythonRunnerEntity.getDependencies().stream().map(TimeDependentEntity::getTimeVariantData).collect(Collectors.toSet());
        if(queryDependencies.isEmpty()) {
            queryDependencies = null;
        }
        List<PythonRunnerEntity> theSamePythonRunnerEntities =
                pythonRunnerRepository.findByPythonCode_TimeVariantDataAndEnvironment_TimeVariantDataAndDependencies_TimeVariantData(
                        pythonRunnerEntity.getPythonCode().getTimeVariantData(),
                        pythonRunnerEntity.getEnvironment().getTimeVariantData(),
                        queryDependencies);
        
        for (PythonRunnerEntity theSamePythonRunnerEntity : theSamePythonRunnerEntities) {

            // following if statements check whether the used files/env is the same as before
            // if not, delete the old python runner and continue check the next one
            if (!theSamePythonRunnerEntity.getPythonCode().isDependencyValid()) {
                this.deletePythonRunnerById(theSamePythonRunnerEntity.getId());
                continue;
            }
            if (!theSamePythonRunnerEntity.getEnvironment().isDependencyValid()) {
                this.deletePythonRunnerById(theSamePythonRunnerEntity.getId());
                continue;
            }
            // the map-reduce return true if the dependencies is empty or all of the elements have good timestamp
            // use orElse(true) as we have done set comparison earlier, two empty sets are considered equal
            if (!theSamePythonRunnerEntity.getDependencies().stream()
                    .map(TimeDependentEntity::isDependencyValid)
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
            pythonRunnerEntity.getPythonCode().getTimeVariantData().writeToFile(this.absoluteWorkDirectory);
            pythonRunnerEntity.getEnvironment().getTimeVariantData().prepareEnvironment(this.absoluteWorkDirectory);
            for (TimeDependentEntity<FileEntity> dependency : pythonRunnerEntity.getDependencies()) {
                dependency.getTimeVariantData().writeToFile(this.absoluteWorkDirectory);
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
