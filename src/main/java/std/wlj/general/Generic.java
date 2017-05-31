package std.wlj.general;

public class Generic<T> {

	T thing;

	public Generic() {
		this.thing = null;
	}

	public Generic(T thing) {
		this.thing = thing;
	}

	@Override
	public String toString() {
		return thing==null ? ("?? --> null") : (thing.getClass().getName() + " --> " + String.valueOf(thing));
	}
}
