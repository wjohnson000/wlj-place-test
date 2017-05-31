package std.wlj.general;

public class MethodData implements Comparable<MethodData> {
    String className;
    String methodName;
    String parameters;

    @Override
    public int compareTo(MethodData that) {
        int compx = this.className.compareToIgnoreCase(that.className);
        if (compx == 0) compx = this.methodName.compareToIgnoreCase(that.methodName);
        if (compx == 0) compx = this.parameters.compareToIgnoreCase(that.parameters);
        return compx;
    }

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder(128);
        buff.append(className);
        buff.append("::").append(methodName);
        buff.append("::[").append(parameters).append("]");
        return buff.toString();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (! (obj instanceof MethodData)) return false;
        return (this.toString().equals(obj.toString()));
    }
}
