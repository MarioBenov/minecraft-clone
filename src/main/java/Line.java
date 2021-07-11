import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Line {
    public static final int vertexCount = 2;
    public static final int FLOAT_SIZE = 4;

    private int vaoHandle = 0;
    private int vboHandle = 0;
    private int posAttribHandle = 0;
    private int colorAttribHandle = 0;
    private int uniUseTexture = 0;

    private Matrix4f transform  = new Matrix4f();

    public Matrix4f getTransform() {
        return transform;
    }

    public Line(Vector3f dir, Vector3f col) {
        vaoHandle = glGenVertexArrays();
        glBindVertexArray(vaoHandle);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer vertices = stack.mallocFloat(vertexCount * 3 * 2); // points * values per attribute * number of attributes

            vertices.put(0f).put(0f).put(0f);
            vertices.put(col.x).put(col.y).put(col.z);

            vertices.put(dir.x).put(dir.y).put(dir.z);
            vertices.put(col.x).put(col.y).put(col.z);

            vertices.flip();

            vboHandle = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vboHandle);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        }

        unbind();
    }

    public void bind() {
        glBindVertexArray(vaoHandle);
        glBindBuffer(GL_ARRAY_BUFFER, vboHandle);
        glUniform1i(uniUseTexture, 0);
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

        uniUseTexture = glGetUniformLocation(shaderProgramHandle, "useTexture");
        glUniform1i(uniUseTexture, 1);

        unbind();
    }
}
