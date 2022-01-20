package com.unascribed.ears.api.features;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.unascribed.ears.api.Slice;

public class AlfalfaData {
	public static final AlfalfaData NONE = new AlfalfaData(0, Collections.<String, Slice>emptyMap());
	
	public final int version;
	public final Map<String, Slice> data;
	
	public AlfalfaData(int version, Map<String, Slice> data) {
		this.version = version;
		this.data = Collections.unmodifiableMap(new HashMap<String, Slice>(data));
	}

	@Override
	public String toString() {
		return "AlfalfaData[version=" + version + ", data=" + data + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + version;
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
		AlfalfaData other = (AlfalfaData) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (version != other.version)
			return false;
		return true;
	}
}
