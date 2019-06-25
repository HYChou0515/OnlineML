package io.hychou.libsvm.predict.service.impl;

import io.hychou.common.exception.service.ServiceException;
import io.hychou.common.exception.service.clienterror.IllegalParameterException;
import io.hychou.common.exception.service.servererror.ServerIOException;
import io.hychou.common.exception.service.servererror.SvmLoadModelException;
import io.hychou.data.entity.DataEntity;
import io.hychou.data.entity.DataPoint;
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

import static io.hychou.data.entity.DataPoint.parseDataPoint;
import static io.hychou.data.util.DataUtils.toSvmNodes;
import static libsvm.svm.svm_load_model;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class PredictServiceImpl implements PredictService {

    private static final Logger logger = getLogger(PredictServiceImpl.class);
    private static final String CANNOT_WRITE_TO_DATA_OUTPUT_STREAM = "Cannot write to DataOutputStream";
    private static final String CANNOT_READ_FROM_BUFFERED_READER = "Cannot read from BufferedReader";
    private static final String CANNOT_CLOSE_DATA_OUTPUT_STREAM = "Cannot close DataOutputStream";
    private static final String CANNOT_CLOSE_BUFFERED_READER = "Cannot close BufferedReader";
    private static final String PREDICTION_NEWLINE = "\n";

    @Autowired
    public PredictServiceImpl() {
        // Default constructor
    }

    private static BufferedReader bufferedReaderForByteArray(byte[] bytes) {
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)));
    }

    private static svm_model byteArrayToSvmModel(ModelEntity modelEntity) throws ServiceException {
        BufferedReader bf = bufferedReaderForByteArray(modelEntity.getDataBytes());
        try {
            return svm_load_model(bf);
        } catch (IOException e) {
            throw new SvmLoadModelException("Cannot convert ModelEntity to svm_model, format not correct?", e);
        }
    }

    private static void predict(BufferedReader input, DataOutputStream output, svm_model model, boolean predictProbability) throws ServiceException {
        PredictStat predictStat = new PredictStat();
        int svmType = svm.svm_get_svm_type(model);
        int nrClass = svm.svm_get_nr_class(model);
        double[] probEstimates = writeAllPossibleLabelsAndGetProbEstimates(output, model, predictProbability, svmType, nrClass);
        while (true) {
            String line;
            try {
                line = input.readLine();
            } catch (IOException e) {
                throw new ServerIOException(CANNOT_READ_FROM_BUFFERED_READER, e);
            }
            if (line == null) break;

            DataPoint dataPoint = parseDataPoint(line);
            double target = dataPoint.getY();
            svm_node[] x = toSvmNodes(dataPoint.getX());

            double predictLabel = predictLabel(output, model, predictProbability, svmType, nrClass, probEstimates, x);
            predictStat.newPredict(predictLabel, target);
        }
        if (predictStat.getTotal() > 0) {
            if (svmType == svm_parameter.EPSILON_SVR ||
                    svmType == svm_parameter.NU_SVR) {
                logger.info("Mean squared error = {}  (regression)", predictStat.getMeanSquareError());
                logger.info("Squared correlation coefficient = {} (regression)", predictStat.getSquaredCorrelationCoefficient());
            } else
                logger.info("Accuracy = {}% ({}/{}) (classification)", predictStat.getAccuracy() * 100, predictStat.getCorrect(), predictStat.getTotal());
        }
    }

    private static double predictLabel(DataOutputStream output, svm_model model, boolean predictProbability, int svmType, int nrClass, double[] probEstimates, svm_node[] x) throws ServiceException {
        double predictLabel;
        if (predictProbability && (svmType == svm_parameter.C_SVC || svmType == svm_parameter.NU_SVC)) {
            predictLabel = svm.svm_predict_probability(model, x, probEstimates);
            try {
                output.writeBytes(String.format("%g", predictLabel));
                for (int j = 0; j < nrClass; j++)
                    output.writeBytes(String.format(" %g", probEstimates[j]));
                output.writeBytes(PREDICTION_NEWLINE);
            } catch (IOException e) {
                throw new ServerIOException(CANNOT_WRITE_TO_DATA_OUTPUT_STREAM);
            }
        } else {
            predictLabel = svm.svm_predict(model, x);
            try {
                output.writeBytes(String.format("%g%s", predictLabel, PREDICTION_NEWLINE));
            } catch (IOException e) {
                throw new ServerIOException(CANNOT_WRITE_TO_DATA_OUTPUT_STREAM);
            }
        }
        return predictLabel;
    }

    private static double[] writeAllPossibleLabelsAndGetProbEstimates(DataOutputStream output, svm_model model, boolean predictProbability, int svmType, int nrClass) throws ServiceException {
        if (predictProbability) {
            if (svmType == svm_parameter.EPSILON_SVR ||
                    svmType == svm_parameter.NU_SVR) {
                logger.info("Prob. model for test data: target value = predicted value + z,");
                logger.info("z: Laplace distribution e^(-|z|/sigma)/(2sigma),sigma={}", svm.svm_get_svr_probability(model));
            } else {
                int[] labels = new int[nrClass];
                svm.svm_get_labels(model, labels);
                double[] probEstimates = new double[nrClass];
                try {
                    output.writeBytes("labels");
                    for (int j = 0; j < nrClass; j++)
                        output.writeBytes(String.format(" %d", labels[j]));
                    output.writeBytes(PREDICTION_NEWLINE);
                } catch (IOException e) {
                    throw new ServerIOException(CANNOT_WRITE_TO_DATA_OUTPUT_STREAM);
                }
                return probEstimates;
            }
        }
        return new double[0];
    }

    @Override
    public PredictionEntity svmPredict(
            DataEntity dataEntity,
            ModelEntity modelEntity,
            LibsvmPredictParameterEntity libsvmPredictParameterEntity
    ) throws ServiceException {
        BufferedReader dataInputReader = bufferedReaderForByteArray(dataEntity.getDataBytes());
        ByteArrayOutputStream baosOfPrediction = new ByteArrayOutputStream();
        DataOutputStream predictionOutputStream = new DataOutputStream(baosOfPrediction);
        svm_model model = byteArrayToSvmModel(modelEntity);
        boolean predictProbability = libsvmPredictParameterEntity.getProbabilityEstimates();
        if (predictProbability && svm.svm_check_probability_model(model) == 0)
            throw new IllegalParameterException("Model does not support probability estimates");
        if (!predictProbability && svm.svm_check_probability_model(model) != 0)
            logger.warn("Model supports probability estimates, but disabled in prediction");
        predict(dataInputReader, predictionOutputStream, model, predictProbability);
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

    private static class PredictStat {
        private int correct = 0;
        private int total = 0;
        private double error = 0;
        private double sumv = 0;
        private double sumy = 0;
        private double sumvv = 0;
        private double sumyy = 0;
        private double sumvy = 0;

        void newPredict(double predictLabel, double target) {
            if (predictLabel == target)
                ++correct;
            error += (predictLabel - target) * (predictLabel - target);
            sumv += predictLabel;
            sumy += target;
            sumvv += predictLabel * predictLabel;
            sumyy += target * target;
            sumvy += predictLabel * target;
            ++total;
        }

        double getMeanSquareError() {
            return error / total;
        }

        double getSquaredCorrelationCoefficient() {
            return ((total * sumvy - sumv * sumy) * (total * sumvy - sumv * sumy)) /
                    ((total * sumvv - sumv * sumv) * (total * sumyy - sumy * sumy));
        }

        double getAccuracy() {
            return (double) correct / total;
        }

        int getCorrect() {
            return correct;
        }

        int getTotal() {
            return total;
        }
    }
}
