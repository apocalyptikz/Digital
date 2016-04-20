package de.neemann.digital.analyse;

import de.neemann.digital.core.Model;
import de.neemann.digital.core.NodeException;
import de.neemann.digital.lang.Lang;

import java.util.ArrayList;

/**
 * Analyses a given model.
 * Calculates the truth table which generated by the given model
 *
 * @author hneemann
 */
public class ModelAnalyser {

    private final Model model;
    private final ArrayList<Model.Signal> inputs;
    private final ArrayList<Model.Signal> outputs;
    private final int rows;
    private final int cols;
    private final boolean[][] data;


    /**
     * Creates a new instance
     *
     * @param model the model
     * @throws AnalyseException AnalyseException
     */
    public ModelAnalyser(Model model) throws AnalyseException {
        this.model = model;
        inputs = checkBinary(model.getInputs());
        if (inputs.size() == 0)
            throw new AnalyseException(Lang.get("err_analyseNoInputs"));
        outputs = checkBinary(model.getOutputs());
        if (outputs.size() == 0)
            throw new AnalyseException(Lang.get("err_analyseNoOutputs"));
        rows = 1 << inputs.size();
        cols = inputs.size() + outputs.size();
        data = new boolean[rows][cols];
    }

    private ArrayList<Model.Signal> checkBinary(ArrayList<Model.Signal> list) throws AnalyseException {
        for (Model.Signal s : list)
            if (s.getValue().getBits() != 1)
                throw new AnalyseException(Lang.get("err_analyseValue_N_IsNotBinary", s.getName()));
        return list;
    }

    /**
     * Analyses the circuit
     *
     * @return this for call chaining
     * @throws NodeException NodeException
     */
    public ModelAnalyser analyse() throws NodeException {
        model.init();
        for (int row = 0; row < rows; row++) {
            for (int i = 0; i < inputs.size(); i++) {
                int pos = inputs.size() - 1 - i;
                boolean bool = (row & (1 << pos)) != 0;
                inputs.get(i).getValue().setBool(bool);
                data[row][i] = bool;
            }
            model.doStep();
            for (int i = 0; i < outputs.size(); i++) {
                data[row][inputs.size() + i] = outputs.get(i).getValue().getBool();
            }
        }
        return this;
    }

    /**
     * @return returns the models inputs
     */
    public ArrayList<Model.Signal> getInputs() {
        return inputs;
    }

    /**
     * @return returns the models outputs
     */
    public ArrayList<Model.Signal> getOutputs() {
        return outputs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Model.Signal s : inputs)
            sb.append(s.getName()).append("\t");
        for (Model.Signal s : outputs)
            sb.append(s.getName()).append("\t");
        sb.append('\n');
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (data[row][col])
                    sb.append("1\t");
                else
                    sb.append("0\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * @return the number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * @return the number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * @return returns one value of the truth table
     */
    public int getValue(int rowIndex, int columnIndex) {
        if (data[rowIndex][columnIndex])
            return 1;
        else
            return 0;
    }
}
