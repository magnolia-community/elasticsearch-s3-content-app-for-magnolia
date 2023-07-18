package info.magnolia.forge.universalcontent.elasticsearch.search.entity;

import lombok.Data;

@Data
public class PaginationModel {

	private Integer from;

	private Integer size;

	public PaginationModel() {
		super();
	}

	public PaginationModel(Integer from, Integer size) {
		super();
		this.from = from;
		this.size = size;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((size == null) ? 0 : size.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PaginationModel other = (PaginationModel) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (size == null) {
			if (other.size != null)
				return false;
		} else if (!size.equals(other.size))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PaginationModel [from=" + from + ", size=" + size + "]";
	}

}
