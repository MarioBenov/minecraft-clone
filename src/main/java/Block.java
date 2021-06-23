import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Block {
    private static final int vertexCount = 8;
    public static final int indicesCount = 6 * 2 * 3; // sides * triangles * indices per triangle
    public static final int FLOAT_SIZE = 4;

    private int vaoHandle = 0;
    private int vboHandle = 0;
    private int indicesHandle = 0;
    private int posAttribHandle = 0;
    private int colorAttribHandle = 0;

    private Matrix4f transform  = new Matrix4f();

    public Matrix4f getTransform() {
        return transform;
    }

    public Block() {
        vaoHandle = glGenVertexArrays();
        glBindVertexArray(vaoHandle);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer vertices = stack.mallocFloat(vertexCount * 3 * 2); // points * values per attribute * number of attributes

            vertices.put(-0.5f).put(-0.5f).put(-0.5f); // BLK
            vertices.put(0.5f).put(0.1f).put(0.1f);

            vertices.put(0.5f).put(-0.5f).put(-0.5f); // BRK
            vertices.put(0.1f).put(0.5f).put(0.1f);

            vertices.put(0.5f).put(0.5f).put(-0.5f); // TRK
            vertices.put(0.1f).put(0.1f).put(0.5f);

            vertices.put(-0.5f).put(0.5f).put(-0.5f); // TLK
            vertices.put(0.2f).put(0.2f).put(0.2f);

            vertices.put(-0.5f).put(-0.5f).put(0.5f); // BLF
            vertices.put(0.2f).put(0.6f).put(0.2f);

            vertices.put(0.5f).put(-0.5f).put(0.5f); // BRF
            vertices.put(0.6f).put(0.2f).put(0.2f);

            vertices.put(0.5f).put(0.5f).put(0.5f); // TRF
            vertices.put(0.3f).put(0.3f).put(0.3f);

            vertices.put(-0.5f).put(0.5f).put(0.5f); // TLF
            vertices.put(0.2f).put(0.2f).put(0.6f);

            vertices.flip();

            vboHandle = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vboHandle);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        }

        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer indices = stack.mallocInt(indicesCount);

            indices.put(0).put(1).put(2);
            indices.put(2).put(3).put(0);

            indices.put(1).put(5).put(6);
            indices.put(6).put(2).put(1);

            indices.put(5).put(4).put(7);
            indices.put(7).put(6).put(5);

            indices.put(4).put(0).put(3);
            indices.put(3).put(7).put(4);

            indices.put(0).put(1).put(5);
            indices.put(5).put(4).put(0);

            indices.put(3).put(2).put(6);
            indices.put(6).put(7).put(3);

            indices.flip();

            indicesHandle = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesHandle);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        }

        unbind();
    }

    public void bind() {
        glBindVertexArray(vaoHandle);
        glBindBuffer(GL_ARRAY_BUFFER, vboHandle);
    }

    public void unbind() {
        glBindVertexArray(0);
    }

    public void initShader(int shaderProgramHandle) {
        bind();

        posAttribHandle = glGetAttribLocation(shaderProgramHandle, "position");
        glEnableVertexAttribArray(posAttribHandle);
        glVertexAttribPointer(posAttribHandle, 3, GL_FLOAT, false, 6 * FLOAT_SIZE, 0);

        colorAttribHandle = glGetAttribLocation(shaderProgramHandle, "color");
        glEnableVertexAttribArray(colorAttribHandle);
        glVertexAttribPointer(colorAttribHandle, 3, GL_FLOAT, false, 6 * FLOAT_SIZE, 3 * FLOAT_SIZE);

        unbind();
    }
}
