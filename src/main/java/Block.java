import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load;

public class Block {
    private static final int vertexCount = 16;
    public static final int indicesCount = 6 * 2 * 3; // sides * triangles * indices per triangle
    public static final int FLOAT_SIZE = 4;

    private int vaoHandle = 0;
    private int vboHandle = 0;
    private int indicesHandle = 0;
    private int posAttribHandle = 0;
//    private int colorAttribHandle = 0;
    private int uniUseTexture = 0;
    private int textureAttribHandle = 0;

    private Matrix4f transform  = new Matrix4f();

    public Matrix4f getTransform() {
        return transform;
    }

    private static final Vector3f tint = new Vector3f(1f, 1f, 1f);

    public Block() {
        vaoHandle = glGenVertexArrays();
        glBindVertexArray(vaoHandle);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer vertices = stack.mallocFloat(vertexCount * (3 + 2)); // points * values per attribute * number of attributes

            vertices.put(-0.5f).put(-0.5f).put(-0.5f); // BLK
            vertices.put(0f).put(0f);
            vertices.put(-0.5f).put(0.5f).put(-0.5f); // TLK
            vertices.put(0f).put(1f);
            vertices.put(0.5f).put(-0.5f).put(-0.5f); // BRK
            vertices.put(1f).put(0f);
            vertices.put(0.5f).put(0.5f).put(-0.5f); // TRK
            vertices.put(1f).put(1f);

            vertices.put(-0.5f).put(-0.5f).put(0.5f); // BLF
            vertices.put(1f).put(0f);
            vertices.put(-0.5f).put(0.5f).put(0.5f); // TLF
            vertices.put(1f).put(1f);
            vertices.put(0.5f).put(-0.5f).put(0.5f); // BRF
            vertices.put(0f).put(0f);
            vertices.put(0.5f).put(0.5f).put(0.5f); // TRF
            vertices.put(0f).put(1f);

            vertices.put(-0.5f).put(0.5f).put(-0.5f); // TLK
            vertices.put(0f).put(0f);
            vertices.put(0.5f).put(0.5f).put(-0.5f); // TRK
            vertices.put(0f).put(1f);
            vertices.put(-0.5f).put(0.5f).put(0.5f); // TLF
            vertices.put(1f).put(0f);
            vertices.put(0.5f).put(0.5f).put(0.5f); // TRF
            vertices.put(1f).put(1f);

            vertices.put(0.5f).put(-0.5f).put(-0.5f); // BRK
            vertices.put(0f).put(0f);
            vertices.put(-0.5f).put(-0.5f).put(-0.5f); // BLK
            vertices.put(0f).put(1f);
            vertices.put(0.5f).put(-0.5f).put(0.5f); // BRF
            vertices.put(1f).put(0f);
            vertices.put(-0.5f).put(-0.5f).put(0.5f); // BLF
            vertices.put(1f).put(1f);

            vertices.flip();

            vboHandle = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vboHandle);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        }

        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer indices = stack.mallocInt(indicesCount);

            indices.put(0).put(1).put(2); // Back
            indices.put(2).put(3).put(1);

            indices.put(4).put(5).put(6); // Front
            indices.put(6).put(7).put(5);

            indices.put(0).put(1).put(4); // Left
            indices.put(4).put(5).put(1);

            indices.put(6).put(7).put(2); // Right
            indices.put(3).put(2).put(7);

            indices.put(8).put(9).put(10); // Top
            indices.put(10).put(11).put(9);

            indices.put(12).put(13).put(14); // Bottom
            indices.put(14).put(15).put(13);

            indices.flip();

            indicesHandle = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesHandle);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        }

        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            URL dirtPath = getClass().getResource("textures/dirt.png");
            ByteBuffer image = stbi_load(dirtPath.getFile().substring(1), w, h, comp, 4);
            if (image == null) {
                throw new RuntimeException("Failed to load a texture file!"
                        + System.lineSeparator() + stbi_failure_reason());
            }

            int width = w.get();
            int height = h.get();

            int textureHandle = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, textureHandle);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            glGenerateMipmap(GL_TEXTURE_2D);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
        }

        unbind();
    }

    public void bind() {
        glBindVertexArray(vaoHandle);
        glBindBuffer(GL_ARRAY_BUFFER, vboHandle);
        glUniform1i(uniUseTexture, 1);
    }

    public void unbind() {
        glBindVertexArray(0);
    }

    public void initShader(int shaderProgramHandle) {
        bind();

        posAttribHandle = glGetAttribLocation(shaderProgramHandle, "position");
        glEnableVertexAttribArray(posAttribHandle);
        glVertexAttribPointer(posAttribHandle, 3, GL_FLOAT, false, (3 + 2) * FLOAT_SIZE, 0);

//        colorAttribHandle = glGetAttribLocation(shaderProgramHandle, "color");
//        glEnableVertexAttribArray(colorAttribHandle);
//        glVertexAttribPointer(colorAttribHandle, 3, GL_FLOAT, false, 6 * FLOAT_SIZE, 3 * FLOAT_SIZE);

        textureAttribHandle = glGetAttribLocation(shaderProgramHandle, "texcoord");
        glEnableVertexAttribArray(textureAttribHandle);
        glVertexAttribPointer(textureAttribHandle, 2, GL_FLOAT, false, (3 + 2) * Float.BYTES, 3 * Float.BYTES);

        int uniTex = glGetUniformLocation(shaderProgramHandle, "myTexImage");
        glUniform1i(uniTex, 0);

        int tintUniform = glGetUniformLocation(shaderProgramHandle, "tintColor");
        glUniform3f(tintUniform, tint.x, tint.y, tint.z);

        uniUseTexture = glGetUniformLocation(shaderProgramHandle, "useTexture");
        glUniform1i(uniUseTexture, 1);

        unbind();
    }
}
