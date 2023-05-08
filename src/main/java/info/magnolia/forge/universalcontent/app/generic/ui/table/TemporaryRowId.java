package info.magnolia.forge.universalcontent.app.generic.ui.table;

public class TemporaryRowId extends RowId {
	private static final long serialVersionUID = -641983830469018329L;

	public TemporaryRowId(Object... id) {
		super(id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(TemporaryRowId.class.equals(obj.getClass()))) {
			return false;
		}
		Object[] compId = ((TemporaryRowId) obj).getId();
		return id.equals(compId);
	}

	@Override
	public String toString() {
		return "Temporary row id";
	}

}
