package io.hychou.libsvm.train.service.impl;

import io.hychou.common.exception.ServiceException;
import io.hychou.common.exception.clienterror.IllegalParameterException;
import io.hychou.common.exception.servererror.FileSystemReadException;
import io.hychou.common.exception.servererror.FileSystemWriteException;
import io.hychou.data.entity.DataEntity;
import io.hychou.data.service.DataService;
import io.hychou.libsvm.model.entity.ModelEntity;
import io.hychou.libsvm.parameter.LibsvmParameterEntity;
import io.hychou.libsvm.train.service.TrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import libsvm.*;

import java.io.*;
import java.nio.file.Files;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.Vector;

@PropertySource("classpath:application.properties")
@Service
public class TrainServiceImpl implements TrainService {

    @Value("${filesystem.path.tmp}")
    private String SERVICE_TMP_DIR;

    private final DataService dataService;

    @Autowired
    public TrainServiceImpl(DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public ModelEntity svmTrain(String dataName, LibsvmParameterEntity libsvmParameterEntity) throws ServiceException {
        svm_parameter param = libsvmParameterEntity.toSvmParameter(defaultParameter());
        svm_problem prob;
        try {
            prob = readProblemAndAdjustParameter(dataService.readDataByName(dataName), param);
        } catch (IOException | NumberFormatException e) {
            // TODO: data format exception should be checked in DataService, not here
            throw new IllegalParameterException("Data format not correct", e);
        }
        String errorMessage = svm.svm_check_parameter(prob, param);
        if (Objects.nonNull(errorMessage)) {
            throw new IllegalParameterException("Parameter format not correct: " + errorMessage);
        }
        svm_model model = svm.svm_train(prob, param);
        String tmpFilePath = SERVICE_TMP_DIR + "123";
        try {
            svm.svm_save_model(tmpFilePath, model);
        } catch (IOException e) {
            throw new FileSystemWriteException("Cannot write model into path " + tmpFilePath, e);
        }

        byte[] modelBytes;
        try {
            modelBytes = Files.readAllBytes(new File(tmpFilePath).toPath());
        } catch (IOException e) {
            throw new FileSystemReadException("Cannot read model from path " + tmpFilePath, e);
        }

        ModelEntity modelEntity = new ModelEntity();
        modelEntity.setDataBytes(modelBytes);
        return modelEntity;
    }


    private static double atof(String s) {
        double d = Double.valueOf(s);
        if (Double.isNaN(d) || Double.isInfinite(d))
        {
            throw new NumberFormatException("NaN or Infinity in input");
        }
        return(d);
    }

    private static int atoi(String s) {
        return Integer.parseInt(s);
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

            StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");

            vy.addElement(atof(st.nextToken()));
            int m = st.countTokens()/2;
            svm_node[] x = new svm_node[m];
            for(int j=0;j<m;j++)
            {
                x[j] = new svm_node();
                x[j].index = atoi(st.nextToken());
                x[j].value = atof(st.nextToken());
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

    private static svm_parameter defaultParameter() {
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
