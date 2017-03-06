package optic.math;

import optic.framework.BufferableData;

import java.nio.FloatBuffer;


/**
 * Visit https://github.com/integeruser/containers for project info, updates and license terms.
 * 
 * @author integeruser
 */
public class Mat3 extends BufferableData<FloatBuffer> {
	public static final int SIZE = (9 * Float.SIZE) / Byte.SIZE;

	
	public float matrix[];

	
	public Mat3() {
		matrix = new float[9];
		matrix[0] = 1.0f;
		matrix[4] = 1.0f;
		matrix[8] = 1.0f;
	}

	public Mat3(float diagonal) {
		matrix = new float[9];
		matrix[0] = diagonal;
		matrix[4] = diagonal;
		matrix[8] = diagonal;
	}

	public Mat3(Mat3 mat) {
		matrix = new float[9];
		System.arraycopy(mat.matrix, 0, matrix, 0, 9);
	}
	
	public Mat3(Mat4 mat) {
		matrix = new float[9];
		matrix[0] = mat.matrix[0];
		matrix[1] = mat.matrix[1];
		matrix[2] = mat.matrix[2];
		matrix[3] = mat.matrix[4];
		matrix[4] = mat.matrix[5];
		matrix[5] = mat.matrix[6];
		matrix[6] = mat.matrix[8];
		matrix[7] = mat.matrix[9];
		matrix[8] = mat.matrix[10];
	}
	
	
	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */	
	
	@Override
	public FloatBuffer fillBuffer(FloatBuffer buffer) {
		buffer.put(matrix);
		
		return buffer;
	}
	
	
	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	
	public void set(int index, float value) {
		matrix[index] = value;
	}
	
	public void set(int columnIndex, int rowIndex, float value) {
		matrix[columnIndex * 3 + rowIndex] = value;
	}
	
	
	public Vec3 getColumn(int columnIndex) {		
		int offset = (columnIndex * 3);
		
		Vec3 res = new Vec3();
		res.x = matrix[offset];
		res.y = matrix[offset + 1];
		res.z = matrix[offset + 2];
		
		return res;
	}
		
	public void setColumn(int columnIndex, Vec3 vec) {
		int offset = (columnIndex * 3);
		
		matrix[offset]     = vec.x;
		matrix[offset + 1] = vec.y;
		matrix[offset + 2] = vec.z;
	}
	
	
	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	
	public Mat3 scale(float scalar) {
		for (int i = 0; i < 9; i++) {
			matrix[i] = matrix[i] * scalar;
		}
		
		return this;
	}


    public static Vec3 mul(Mat3 mat, Vec3 vec) {
        Vec3 res = new Vec3();

        for (int i = 0; i < 3; i++) {
            float temp = 0;
            switch (i) {
                case 0:
                    temp = vec.x;
                    break;
                case 1:
                    temp = vec.y;
                    break;
                case 2:
                    temp = vec.z;
                    break;
            }

            res.x += mat.matrix[3*i + 0] * temp;
            res.y += mat.matrix[3*i + 1] * temp;
            res.z += mat.matrix[3*i + 2] * temp;

        }

        return res;
    }
}