package io.hychou.runnable.python.runner.profile.entity;

import io.hychou.common.Constant;
import io.hychou.common.SignificantField;
import io.hychou.common.datastructure.AbstractCrudDataStructure;
import io.hychou.file.entity.FileEntity;
import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlEntity;
import io.hychou.runnable.python.runner.RunnerStateEnum;
import io.hychou.runnable.timedependent.entity.TimeDependentEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Entity
public class PythonRunnerProfileEntity extends AbstractCrudDataStructure {
    @Id
    @GeneratedValue
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    @ManyToOne(cascade = CascadeType.ALL)
    private TimeDependentEntity<FileEntity> pythonCode;

    @Getter
    @Setter
    @ManyToMany(cascade = CascadeType.ALL)
    private Set<TimeDependentEntity<FileEntity>> dependencies;

    @Getter
    @Setter
    @ManyToOne(cascade = CascadeType.ALL)
    private TimeDependentEntity<AnacondaYamlEntity> environment;

    @Getter
    @Setter
    private FileEntity result;

    @Getter
    @Setter
    @Column(length = Constant.MB)
    private String summary;

    @Getter
    @Setter
    @ElementCollection
    @Column(length = Constant.MB)
    private List<String> errorMessages;

    @Getter
    private RunnerStateEnum state;

    @Getter
    private Date createdTimestamp;

    @Getter
    private Date preparingTimestamp;

    @Getter
    private Date runningTimestamp;

    @Getter
    private Date cleaningTimestamp;

    @Getter
    private Date finishedTimestamp;

    public PythonRunnerProfileEntity() {
        state = RunnerStateEnum.CREATED;
        createdTimestamp = new Date();
        errorMessages = new ArrayList<>();
    }

    public PythonRunnerProfileEntity(FileEntity pythonCode, List<FileEntity> dependencies, AnacondaYamlEntity environment) {
        this();
        this.pythonCode = new TimeDependentEntity<>(pythonCode);
        this.dependencies = new HashSet<>();
        for (FileEntity dependency : dependencies) {
            this.dependencies.add(new TimeDependentEntity<>(dependency));
        }
        this.environment = new TimeDependentEntity<>(environment);
    }

    public void toPreparingState() {
        this.preparingTimestamp = new Date();
        this.state = RunnerStateEnum.PREPARING;
    }

    public void toRunningState() {
        this.runningTimestamp = new Date();
        this.state = RunnerStateEnum.RUNNING;
    }

    public void toCleaningState() {
        this.cleaningTimestamp = new Date();
        this.state = RunnerStateEnum.CLEANING;
    }

    public void toFinishedState() {
        this.finishedTimestamp = new Date();
        this.state = RunnerStateEnum.FINISHED;
    }

    public void toCrashedState() {
        this.finishedTimestamp = new Date();
        this.state = RunnerStateEnum.CRASHED;
    }

    public void addErrorMessage(String errorMessage) {
        this.errorMessages.add("Error on state=" + this.state + ", " + errorMessage);
    }

    public boolean isFinished() {
        return state == RunnerStateEnum.FINISHED;
    }

    @Override
    public List<SignificantField> significantFields() {
        List<SignificantField> fields = new ArrayList<>();
        fields.add(new SignificantField("id", this.id));
        fields.add(new SignificantField("pythonCode", this.pythonCode));
        fields.add(new SignificantField("dependencies", this.dependencies));
        fields.add(new SignificantField("environment", this.environment));
        fields.add(new SignificantField("state", this.state));
        fields.add(new SignificantField("result", this.result));
        fields.add(new SignificantField("summary", this.summary));
        fields.add(new SignificantField("createdTimestamp", this.createdTimestamp));
        fields.add(new SignificantField("preparingTimestamp", this.preparingTimestamp));
        fields.add(new SignificantField("runningTimestamp", this.runningTimestamp));
        fields.add(new SignificantField("cleaningTimestamp", this.cleaningTimestamp));
        fields.add(new SignificantField("finishedTimestamp", this.finishedTimestamp));
        return fields;
    }
}
