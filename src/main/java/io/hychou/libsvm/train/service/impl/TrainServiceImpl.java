package io.hychou.libsvm.train.service.impl;

import io.hychou.common.exception.service.ServiceException;
import io.hychou.common.exception.service.clienterror.IllegalParameterException;
import io.hychou.common.exception.service.servererror.FileSystemReadException;
import io.hychou.common.exception.service.servererror.FileSystemWriteException;
import io.hychou.data.entity.DataEntity;
import io.hychou.data.util.DataUtils;
import io.hychou.libsvm.model.entity.ModelEntity;
import io.hychou.libsvm.parameter.LibsvmTrainParameterEntity;
import io.hychou.libsvm.train.service.TrainService;
import libsvm.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.Vector;

import static io.hychou.common.Constant.LIBSVM_DELIMITERS;

@Service
public class TrainServiceImpl implements TrainService {

    private final long collisionMax;
    private static final String MODEL_EXTENSION = ".model";
    private static final svm_parameter DEFAULT_PARAMETER = getDefaultParameter();

    @Autowired
    public TrainServiceImpl(@Value("#{new Long('${filesystem.path.hash.collision}')}") long collisionMax) {
        this.collisionMax = collisionMax;
    }

    @Override
    public ModelEntity svmTrain(DataEntity dataEntity, LibsvmTrainParameterEntity libsvmTrainParameterEntity) throws ServiceException {
        // Prepare svm parameter
        svm_parameter param = libsvmTrainParameterEntity.toSvmParameter(DEFAULT_PARAMETER);
        // Prepare svm problem
        svm_problem prob;
        try {
            prob = readProblemAndAdjustParameter(dataEntity, param);
        } catch (IOException e) {
            throw new IllegalParameterException("Data format not correct", e);
        }
        // Check svm settings: problem and parameter
        String errorMessage = svm.svm_check_parameter(prob, param);
        if (Objects.nonNull(errorMessage)) {
            throw new IllegalParameterException("Parameter format not correct: " + errorMessage);
        }

        // svm train
        svm_model model = svm.svm_train(prob, param);

        // Prepare byte array of model
        byte[] modelBytes = getModelByteArray(model);

        // Prepare model entity
        ModelEntity modelEntity = new ModelEntity();
        modelEntity.setDataBytes(modelBytes);
        return modelEntity;
    }

    private byte[] getModelByteArray(svm_model model) throws ServiceException {
        String tmpFilePath;
        try {
            tmpFilePath= getUniqueModelFilePath(System.getProperty("java.io.tmpdir"), model, this.collisionMax);
        } catch (IOException e) {
            throw new FileSystemWriteException("Model cannot be written", e);
        }
        try {
            svm.svm_save_model(tmpFilePath, model);
        } catch (IOException e) {
            throw new FileSystemWriteException("Cannot write model into path: " + tmpFilePath, e);
        }
        try {
            return Files.readAllBytes(new File(tmpFilePath).toPath());
        } catch (IOException e) {
            throw new FileSystemReadException("Cannot read model from path: " + tmpFilePath, e);
        }
    }

    private static String getUniqueModelFilePath(String serviceTmpDir, svm_model model, long collisionMax) throws IOException{
        int trial = 0;
        while(trial++ < collisionMax) {
            double randomSeed = Math.random();
            int hash = Objects.hash(
                    Arrays.hashCode(model.getClass().getDeclaredFields()),
                    randomSeed
            );
            String sha256hex = DigestUtils.sha256Hex(String.valueOf(hash));
            String tmpFilePath = Paths.get(serviceTmpDir, sha256hex + MODEL_EXTENSION).toString();
            File f = new File(tmpFilePath);
            if(!f.exists()) {
                return tmpFilePath;
            }
        }
        throw new IOException("File name collision over " + collisionMax + " times");
    }

    private static svm_problem readProblemAndAdjustParameter(DataEntity dataEntity, svm_parameter param) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(dataEntity.getDataBytes());
        BufferedReader fp = new BufferedReader(new InputStreamReader(inputStream));
        Vector<Double> vy = new Vector<>();
        Vector<svm_node[]> vx = new Vector<>();
        int max_index = 0;

        while(true)
        {
            String line = fp.readLine();
            if(line == null) break;

            StringTokenizer st = new StringTokenizer(line,LIBSVM_DELIMITERS);

            vy.addElement(DataUtils.atof(st.nextToken()));
            int m = st.countTokens()/2;
            svm_node[] x = new svm_node[m];
            for(int j=0;j<m;j++)
            {
                x[j] = new svm_node();
                x[j].index = DataUtils.atoi(st.nextToken());
                x[j].value = DataUtils.atof(st.nextToken());
            }
            if(m>0) max_index = Math.max(max_index, x[m-1].index);
            vx.addElement(x);
        }

        svm_problem prob = new svm_problem();
        prob.l = vy.size();
        prob.x = new svm_node[prob.l][];
        for(int i=0;i<prob.l;i++)
            prob.x[i] = vx.elementAt(i);
        prob.y = new double[prob.l];
        for(int i=0;i<prob.l;i++)
            prob.y[i] = vy.elementAt(i);

        if(param.gamma == 0 && max_index > 0)
            param.gamma = 1.0/max_index;

        fp.close();
        return prob;
    }

    private static svm_parameter getDefaultParameter() {
        svm_parameter param = new svm_parameter();
        // default values
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.RBF;
        param.degree = 3;
        param.gamma = 0;	// 1/num_features
        param.coef0 = 0;
        param.nu = 0.5;
        param.cache_size = 100;
        param.C = 1;
        param.eps = 1e-3;
        param.p = 0.1;
        param.shrinking = 1;
        param.probability = 0;
        param.nr_weight = 0;
        param.weight_label = new int[0];
        param.weight = new double[0];
        return param;
    }
}
