package Engine;

public class Square {

    public enum Type {
        Mine, Empty, Hint
    }

    public enum State {
        Open, Close
    }

    public enum Flag {
        Yes, No
    }

    private int value;
    private Type type;
    private State state;
    private Flag flag;

    public Square(int value, Type type, State state) {
        this.value = value;
        this.type = type;
        this.state = state;
        this.flag = Flag.No;
    }

    public void setFlag(Flag flag) {
        this.flag = flag;
    }

    public Flag getFlag() {

        return flag;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void incrementValue() {
        this.value++;
    }

    public void incrementValueHint() {
        this.incrementValue();
        this.setType(Square.Type.Hint);
    }

    public Type getType() {
        return type;
    }

    public State getState() {
        return state;
    }
}