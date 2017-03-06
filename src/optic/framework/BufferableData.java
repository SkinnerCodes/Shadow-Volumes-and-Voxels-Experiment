package optic.framework;

import java.nio.Buffer;


/**
 * Visit https://github.com/integeruser/containers for project info, updates and license terms.
 * 
 * @author integeruser
 */
public abstract class BufferableData<T extends Buffer> {		
	
	public abstract T fillBuffer(T buffer);
	
	public T fillAndFlipBuffer(T buffer) {
		buffer.clear();
		fillBuffer(buffer);
		buffer.flip();
		
		return buffer;
	}
}