package main.java.colormatcher.applogic;

public class NeuralNet {

    /*
    Training data

    Input : Colors are coded using values RGB which are in range [0, 1]
            3 zeros means black
            3 ones means white

    Output : First number indicate white second black
    */
    private final double LEARNING_RATE = 0.9;
    private final double[][] X = new double[][]{{0.6, 0.6, 0.6},
                                                {0.4, 0.4, 0.4}};
    private final double[][] DESIRED_OUTPUT = new double[][]{{0.0, 1.0},
                                                             {1.0, 0.0}};

    private Synapse syn0;
    private Synapse syn1;

    public NeuralNet() {
        syn0 = new Synapse(3, 2);
        syn1 = new Synapse(2, 2);

        for (int i = 0; i < 6000; i++) {
            train(X[i % 2], DESIRED_OUTPUT[i % 2]);
        }
    }

    public NeuralNetLayer think(double[] X) {
        NeuralNetLayer hiddenLayer = computeHiddenLayer(X);
        NeuralNetLayer outputLayer = computeOutputLayer(hiddenLayer);

        return outputLayer;
    }

    private void train(double[] X, double[] d) {
        NeuralNetLayer hiddenLayer = computeHiddenLayer(X);
        NeuralNetLayer outputLayer = computeOutputLayer(hiddenLayer);

        backPropagation(outputLayer, hiddenLayer, X, d);
    }

    private NeuralNetLayer computeHiddenLayer(double[] X){
        NeuralNetLayer retv = new NeuralNetLayer(2);
        double sum = 0.0;
        for (int i = 0; i < X.length; i++) {
            sum += X[i] * syn0.getWeight(i, 0);
        }
        retv.setNodeValue(0, sigmoid(sum));
        sum = 0.0;
        for (int i = 0; i < X.length; i++) {
            sum += X[i] * syn0.getWeight(i, 1);
        }
        retv.setNodeValue(1, sigmoid(sum));

        return retv;
    }

    private NeuralNetLayer computeOutputLayer(NeuralNetLayer hiddenLayer){
        NeuralNetLayer retv = new NeuralNetLayer(2);
        double sum = 0.0;
        for(int i = 0; i < hiddenLayer.length(); i++){
            sum += hiddenLayer.getNodeValue(i) * syn1.getWeight(i, 0);
        }
        retv.setNodeValue(0, sigmoid(sum));

        sum = 0.0;
        for(int i = 0; i < hiddenLayer.length(); i++){
            sum += hiddenLayer.getNodeValue(i) * syn1.getWeight(i, 1);
        }
        retv.setNodeValue(1, sigmoid(sum));

        return retv;
    }

    private void backPropagation(NeuralNetLayer outputLayer, NeuralNetLayer hiddenLayer, double[] X, double[] desiredOut) {
        double[] outputError = new double[2];
        outputError[0] = desiredOut[0] - outputLayer.getNodeValue(0);
        outputError[1] = desiredOut[1] - outputLayer.getNodeValue(1);

        double[] outputDelta = new double[]{(outputError[0] * sigmoidDerivative(outputLayer.getNodeValue(0))),
                (outputError[1] * sigmoidDerivative(outputLayer.getNodeValue(1)))};

        double[][] syn1Delta = new double[2][2];
        for(int i = 0; i < syn1Delta.length; i++) {
            for (int j = 0; j < syn1Delta[i].length; j++) {
                syn1Delta[j][i] = outputDelta[i] * hiddenLayer.getNodeValue(j);
            }
        }

        double[] hiddenDelta = new double[]{((outputDelta[0] * syn1.getWeight(0, 0) + outputDelta[1] * syn1.getWeight(0, 1)) * sigmoidDerivative(hiddenLayer.getNodeValue(0))),
                                             ((outputDelta[0] * syn1.getWeight(1, 0) + outputDelta[1] * syn1.getWeight(1, 1)) * sigmoidDerivative(hiddenLayer.getNodeValue(1)))};

        double[][] syn0Delta = new double[3][2];
        for (int i = 0; i < syn1.getRow(); i++) {
            for (int j = 0; j < syn1.getCol(); j++) {
                syn0Delta[i][j] = hiddenDelta[j] * X[i];
            }
        }

        for (int i = 0; i < syn1.getRow(); i++) {
            for (int j = 0; j < syn1.getCol(); j++) {
                syn1.setWeight(i, j,  syn1.getWeight(i, j) + LEARNING_RATE * syn1Delta[i][j]);
            }
        }

        for (int i = 0; i < syn0.getRow(); i++) {
            for (int j = 0; j < syn0.getCol(); j++) {
                syn0.setWeight(i, j,  syn0.getWeight(i, j) + LEARNING_RATE * syn0Delta[i][j]);
            }
        }
    }

    private final double sigmoid(final double x) {
        return 1 / (1 + Math.exp(-x));
    }

    private final double sigmoidDerivative(final double x) {
        return x * (1 - x);
    }
}