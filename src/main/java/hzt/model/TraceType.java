package hzt.model;

public class TraceType {

    private final String name;
    private final TraceXY traceXY;

    public TraceType(String name, TraceXY traceXY) {
        this.name = name;
        this.traceXY = traceXY;
    }

    public TraceXY getTraceXY() {
        return traceXY;
    }

    public int traceSize() {
        return traceXY.size();
    }

    @Override
    public String toString() {
        return name;
    }
}
