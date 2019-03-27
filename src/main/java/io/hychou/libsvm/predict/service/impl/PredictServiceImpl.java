package io.hychou.libsvm.predict.service.impl;

import io.hychou.common.exception.service.ServiceException;
import io.hychou.common.exception.service.clienterror.IllegalParameterException;
import io.hychou.common.exception.service.servererror.ServerIOException;
import io.hychou.common.exception.service.servererror.SvmLoadModelException;
import io.hychou.data.entity.DataEntity;
import io.hychou.libsvm.model.entity.ModelEntity;
import io.hychou.libsvm.parameter.LibsvmPredictParameterEntity;
import io.hychou.libsvm.predict.service.PredictService;
import io.hychou.libsvm.prediction.entity.PredictionEntity;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.StringTokenizer;

import static io.hychou.data.util.DataUtils.atof;
import static io.hychou.data.util.DataUtils.atoi;
import static libsvm.svm.svm_load_model;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class PredictServiceImpl implements PredictService {

    private final static Logger logger = getLogger(PredictServiceImpl.class);
    private final static String CANNOT_WRITE_TO_DATA_OUTPUT_STREAM = "Cannot write to DataOutputStream";
    private final static String CANNOT_READ_FROM_BUFFERED_READER = "Cannot read from BufferedReader";
    private final static String CANNOT_CLOSE_DATA_OUTPUT_STREAM = "Cannot close DataOutputStream";
    private final static String CANNOT_CLOSE_BUFFERED_READER = "Cannot close BufferedReader";

    @Autowired
    public PredictServiceImpl() {}

    @Override
    public PredictionEntity svmPredict(
            DataEntity dataEntity,
            ModelEntity modelEntity,
            LibsvmPredictParameterEntity libsvmPredictParameterEntity
    ) throws ServiceException {
        BufferedReader dataInputReader = BufferedReaderForByteArray(dataEntity.getDataBytes());
        ByteArrayOutputStream baosOfPrediction = new ByteArrayOutputStream();
        DataOutputStream predictionOutputStream = new DataOutputStream(baosOfPrediction);
        svm_model model = ByteArrayToSvmModel(modelEntity);
        int predict_probability = libsvmPredictParameterEntity.getProbabilityEstimates() ? 1 : 0;
        if(predict_probability == 1 && svm.svm_check_probability_model(model)==0)
                throw new IllegalParameterException("Model does not support probability estimates");
        if(predict_probability == 0 && svm.svm_check_probability_model(model)!=0)
                logger.warn("Model supports probability estimates, but disabled in prediction");
        predict(dataInputReader, predictionOutputStream, model, predict_probability);
        try {
            predictionOutputStream.flush();
        } catch (IOException e) {
            throw new ServerIOException(CANNOT_WRITE_TO_DATA_OUTPUT_STREAM, e);
        }
        try {
            dataInputReader.close();
        } catch (IOException e) {
            throw new ServerIOException(CANNOT_CLOSE_BUFFERED_READER, e);
        }
        try {
            predictionOutputStream.close();
        } catch (IOException e) {
            throw new ServerIOException(CANNOT_CLOSE_DATA_OUTPUT_STREAM, e);
        }
        return new PredictionEntity(baosOfPrediction.toByteArray());
    }
    private static BufferedReader BufferedReaderForByteArray(byte[] bytes) {
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)));
    }
    private static svm_model ByteArrayToSvmModel(ModelEntity modelEntity) throws ServiceException {
        BufferedReader bf = BufferedReaderForByteArray(modelEntity.getDataBytes());
        try {
            return svm_load_model(bf);
        } catch (IOException e) {
            throw new SvmLoadModelException("Cannot convert ModelEntity to svm_model, format not correct?", e);
        }
    }
    // mainly a copy from libsvm.svm_predict.predict
    private static void predict(BufferedReader input, DataOutputStream output, svm_model model, int predict_probability) throws ServiceException {
        int correct = 0;
        int total = 0;
        double error = 0;
        double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;

        int svm_type= svm.svm_get_svm_type(model);
        int nr_class=svm.svm_get_nr_class(model);
        double[] prob_estimates=null;

        if(predict_probability == 1)
        {
            if(svm_type == svm_parameter.EPSILON_SVR ||
                    svm_type == svm_parameter.NU_SVR)
            {
                logger.info("Prob. model for test data: target value = predicted value + z,");
                logger.info("z: Laplace distribution e^(-|z|/sigma)/(2sigma),sigma={}", svm.svm_get_svr_probability(model));
            }
            else
            {
                int[] labels=new int[nr_class];
                svm.svm_get_labels(model,labels);
                prob_estimates = new double[nr_class];
                try {
                    output.writeBytes("labels");
                    for (int j = 0; j < nr_class; j++)
                        output.writeBytes(" " + labels[j]);
                    output.writeBytes("\n");
                } catch (IOException e) {
                    throw new ServerIOException(CANNOT_WRITE_TO_DATA_OUTPUT_STREAM);
                }
            }
        }
        while(true)
        {
            String line;
            try {
                line = input.readLine();
            } catch (IOException e) {
                throw new ServerIOException(CANNOT_READ_FROM_BUFFERED_READER, e);
            }
            if(line == null) break;

            StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");

            double target = atof(st.nextToken());
            int m = st.countTokens()/2;
            svm_node[] x = new svm_node[m];
            for(int j=0;j<m;j++)
            {
                x[j] = new svm_node();
                x[j].index = atoi(st.nextToken());
                x[j].value = atof(st.nextToken());
            }

            double v;
            if (predict_probability==1 && (svm_type==svm_parameter.C_SVC || svm_type==svm_parameter.NU_SVC))
            {
                v = svm.svm_predict_probability(model,x,prob_estimates);
                try {
                    output.writeBytes(v+" ");
                    for(int j=0;j<nr_class;j++)
                        output.writeBytes(prob_estimates[j]+" ");
                    output.writeBytes("\n");
                } catch (IOException e) {
                    throw new ServerIOException(CANNOT_WRITE_TO_DATA_OUTPUT_STREAM);
                }
            }
            else
            {
                v = svm.svm_predict(model,x);
                try {
                    output.writeBytes(v+"\n");
                } catch (IOException e) {
                    throw new ServerIOException(CANNOT_WRITE_TO_DATA_OUTPUT_STREAM);
                }
            }

            if(v == target)
                ++correct;
            error += (v-target)*(v-target);
            sumv += v;
            sumy += target;
            sumvv += v*v;
            sumyy += target*target;
            sumvy += v*target;
            ++total;
        }
        if(svm_type == svm_parameter.EPSILON_SVR ||
                svm_type == svm_parameter.NU_SVR)
        {
            logger.info("Mean squared error = "+error/total+" (regression)");
            logger.info("Squared correlation coefficient = "+
                    ((total*sumvy-sumv*sumy)*(total*sumvy-sumv*sumy))/
                            ((total*sumvv-sumv*sumv)*(total*sumyy-sumy*sumy))+
                    " (regression)");
        }
        else
            logger.info("Accuracy = "+(double)correct/total*100+
                    "% ("+correct+"/"+total+") (classification)");
    }
}
