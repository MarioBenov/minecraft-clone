import org.joml.Matrix4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {
    private final String vertexShaderSource = "#version 150 core\n" +
            "        \n" +
            "in vec3 position;\n" +
            "in vec3 color;\n" +
            "\n" +
            "out vec3 vertexColor;\n" +
            "\n" +
            "uniform mat4 model;\n" +
            "uniform mat4 view;\n" +
            "uniform mat4 projection;\n" +
            "\n" +
            "void main() {\n" +
            "    vertexColor = color;\n" +
           // "    vertexColor = vec3(0.1, 0.24, 0.15);\n" +
            "    mat4 mvp = projection * view * model;\n" +
            "    gl_Position = mvp * vec4(position, 1.0);\n" +
            "}";
    private final String fragmentShaderSource = "#version 150 core\n" +
            "\n" +
            "in vec3 vertexColor;\n" +
            "\n" +
            "out vec4 fragColor;\n" +
            "\n" +
            "void main() {\n" +
            "    fragColor = vec4(vertexColor, 1.0);\n" +
            "}";

    private void run() {
        init();
        mainLoop();
        cleanup();
    }

    private long window;

    private void initWindow() {
        GLFWErrorCallback.createPrint(System.err).set();
        if( !glfwInit() )
            throw new IllegalStateException("failed to init GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_REFRESH_RATE, 2);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
//        PointerBuffer monitors = glfwGetMonitors();
        window = glfwCreateWindow(vidMode.width(), vidMode.height(), "Minecraft clone", NULL, NULL);
        if(window == NULL)
            throw new IllegalStateException("failed to create window");

        glfwSetWindowPos(window, 1920 + 20, 20);

        glfwMakeContextCurrent(window);
        glfwShowWindow(window);
    }

    private void initGl() {
        GL.createCapabilities();

        glClearColor(0.35f, 0.35f, 0.35f, 0.0f);
        glfwSwapInterval(1);
        glEnable(GL_DEPTH_TEST);
    }

    private void init() {
        initWindow();
        initGl();
    }

    private void mainLoop() {
        int vaoHandle = glGenVertexArrays();
        glBindVertexArray(vaoHandle);

//        int vertexCount = 6 * 2 * 3; // sides * faces per side * points per face
        int vertexCount = 8;
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

//            // FRONT
//            vertices.put(-0.5f).put(-0.5f).put(0.5f); // x y z
//            vertices.put(0.5f).put(0.1f).put(0.1f); // r g b
//
//            vertices.put(0.5f).put(-0.5f).put(0.5f);
//            vertices.put(0.1f).put(0.5f).put(0.1f);
//
//            vertices.put(-0.5f).put(0.5f).put(0.5f);
//            vertices.put(0.1f).put(0.1f).put(0.5f);
//
//            vertices.put(0.5f).put(0.5f).put(0.5f); // x y z
//            vertices.put(0.1f).put(0.5f).put(0.1f); // r g b
//
//            vertices.put(-0.5f).put(0.5f).put(0.5f);
//            vertices.put(0.1f).put(0.5f).put(0.1f);
//
//            vertices.put(0.5f).put(-0.5f).put(0.5f);
//            vertices.put(0.1f).put(0.1f).put(0.5f);
//
//            // BACK
//            vertices.put(-0.5f).put(-0.5f).put(-0.5f); // x y z
//            vertices.put(0.5f).put(0.1f).put(0.1f); // r g b
//
//            vertices.put(0.5f).put(-0.5f).put(-0.5f);
//            vertices.put(0.1f).put(0.5f).put(0.1f);
//
//            vertices.put(-0.5f).put(0.5f).put(-0.5f);
//            vertices.put(0.1f).put(0.1f).put(0.5f);
//
//            vertices.put(0.5f).put(0.5f).put(-0.5f); // x y z
//            vertices.put(0.5f).put(0.1f).put(0.1f); // r g b
//
//            vertices.put(-0.5f).put(0.5f).put(-0.5f);
//            vertices.put(0.1f).put(0.5f).put(0.1f);
//
//            vertices.put(0.5f).put(-0.5f).put(-0.5f);
//            vertices.put(0.1f).put(0.1f).put(0.5f);
//
//            // LEFT
//            vertices.put(-0.5f).put(-0.5f).put(0.5f); // x y z
//            vertices.put(0.5f).put(0.1f).put(0.1f); // r g b
//
//            vertices.put(-0.5f).put(0.5f).put(0.5f);
//            vertices.put(0.1f).put(0.5f).put(0.1f);
//
//            vertices.put(-0.5f).put(-0.5f).put(-0.5f);
//            vertices.put(0.1f).put(0.1f).put(0.5f);
//
//            vertices.put(-0.5f).put(0.5f).put(-0.5f); // x y z
//            vertices.put(0.5f).put(0.1f).put(0.1f); // r g b
//
//            vertices.put(-0.5f).put(-0.5f).put(-0.5f);
//            vertices.put(0.1f).put(0.5f).put(0.1f);
//
//            vertices.put(-0.5f).put(0.5f).put(0.5f);
//            vertices.put(0.1f).put(0.1f).put(0.5f);
//
//            // RIGHT
//            vertices.put(0.5f).put(-0.5f).put(0.5f); // x y z
//            vertices.put(0.5f).put(0.1f).put(0.1f); // r g b
//
//            vertices.put(0.5f).put(-0.5f).put(-0.5f);
//            vertices.put(0.1f).put(0.5f).put(0.1f);
//
//            vertices.put(0.5f).put(0.5f).put(-0.5f);
//            vertices.put(0.1f).put(0.1f).put(0.5f);
//
//            vertices.put(0.5f).put(0.5f).put(0.5f); // x y z
//            vertices.put(0.5f).put(0.1f).put(0.1f); // r g b
//
//            vertices.put(0.5f).put(-0.5f).put(0.5f);
//            vertices.put(0.1f).put(0.5f).put(0.1f);
//
//            vertices.put(0.5f).put(0.5f).put(-0.5f);
//            vertices.put(0.1f).put(0.1f).put(0.5f);
//
//            // TOP
//            vertices.put(-0.5f).put(0.5f).put(0.5f); // x y z
//            vertices.put(0.5f).put(0.1f).put(0.1f); // r g b
//
//            vertices.put(0.5f).put(0.5f).put(0.5f);
//            vertices.put(0.1f).put(0.5f).put(0.1f);
//
//            vertices.put(0.5f).put(0.5f).put(-0.5f);
//            vertices.put(0.1f).put(0.1f).put(0.5f);
//
//            vertices.put(-0.5f).put(0.5f).put(-0.5f); // x y z
//            vertices.put(0.5f).put(0.1f).put(0.1f); // r g b
//
//            vertices.put(-0.5f).put(0.5f).put(0.5f);
//            vertices.put(0.1f).put(0.5f).put(0.1f);
//
//            vertices.put(0.5f).put(0.5f).put(-0.5f);
//            vertices.put(0.1f).put(0.1f).put(0.5f);
//
//            // BOTTOM
//            vertices.put(-0.5f).put(-0.5f).put(0.5f); // x y z
//            vertices.put(0.5f).put(0.1f).put(0.1f); // r g b
//
//            vertices.put(0.5f).put(-0.5f).put(0.5f);
//            vertices.put(0.1f).put(0.5f).put(0.1f);
//
//            vertices.put(0.5f).put(-0.5f).put(-0.5f);
//            vertices.put(0.1f).put(0.1f).put(0.5f);
//
//            vertices.put(-0.5f).put(-0.5f).put(-0.5f); // x y z
//            vertices.put(0.5f).put(0.1f).put(0.1f); // r g b
//
//            vertices.put(-0.5f).put(-0.5f).put(0.5f);
//            vertices.put(0.1f).put(0.5f).put(0.1f);
//
//            vertices.put(0.5f).put(-0.5f).put(-0.5f);
//            vertices.put(0.1f).put(0.1f).put(0.5f);

            vertices.flip();

            int vboHandle = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vboHandle);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        }
//        } finally {
//            stack.pop();
//        }

        int indicesCount = 6 * 2 * 3; // sides * triangles * indices per triangle
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

            int indicesHandle = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesHandle);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        }


        int vertexShaderHandle = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShaderHandle, vertexShaderSource);
        glCompileShader(vertexShaderHandle);

        int status = glGetShaderi(vertexShaderHandle, GL_COMPILE_STATUS);
//        System.out.println(glGetShaderi(vertexShaderHandle, GL_INFO_LOG_LENGTH));
//        System.out.println(glGetShaderInfoLog(vertexShaderHandle));
        if(status == GL_FALSE) {
            throw new RuntimeException("Failed to compile vertex shader");
        }

        int fragmentShaderHandle = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShaderHandle, fragmentShaderSource);
        glCompileShader(fragmentShaderHandle);

        status = glGetShaderi(fragmentShaderHandle, GL_COMPILE_STATUS);
        if(status == GL_FALSE) {
            throw new RuntimeException("Failed to compile fragment shader");
        }

        int shaderProgramHandle = glCreateProgram();
        glAttachShader(shaderProgramHandle, vertexShaderHandle);
        glAttachShader(shaderProgramHandle, fragmentShaderHandle);
        glLinkProgram(shaderProgramHandle);

        status = glGetProgrami(shaderProgramHandle, GL_LINK_STATUS);
        if(status == GL_FALSE) {
            throw new RuntimeException("Failed to link shader program");
        }

        glUseProgram(shaderProgramHandle);


        int FLOAT_SIZE = 4;

        int posAttribHandle = glGetAttribLocation(shaderProgramHandle, "position");
        glEnableVertexAttribArray(posAttribHandle);
        glVertexAttribPointer(posAttribHandle, 3, GL_FLOAT, false, 6 * FLOAT_SIZE, 0);

        int colorAttribHandle = glGetAttribLocation(shaderProgramHandle, "color");
        glEnableVertexAttribArray(colorAttribHandle);
        glVertexAttribPointer(colorAttribHandle, 3, GL_FLOAT, false, 6 * FLOAT_SIZE, 3 * FLOAT_SIZE);



        int uniformModelHandle = glGetUniformLocation(shaderProgramHandle, "model");
        Matrix4f modelMatrix  = new Matrix4f();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer model = modelMatrix
//                    .rotate(45, 1.0f, 1.0f, 0f)
                    .get(stack.mallocFloat(4 * 4));
            glUniformMatrix4fv(uniformModelHandle, false, model);
        }

        int uniformViewHandle = glGetUniformLocation(shaderProgramHandle, "view");
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer model = new Matrix4f()
                    .get(stack.mallocFloat(4 * 4));
            glUniformMatrix4fv(uniformViewHandle, false, model);
        }

        int uniformProjectionHandle = glGetUniformLocation(shaderProgramHandle, "projection");
        try (MemoryStack stack = MemoryStack.stackPush()) {
            float aspectRatio = 1920f / 1080f;
            FloatBuffer model = new Matrix4f()
                    .ortho(-aspectRatio, aspectRatio, -1f, 1f, -1f, 1f)
                    .get(stack.mallocFloat(4 * 4));
            glUniformMatrix4fv(uniformProjectionHandle, false, model);
        }


//        Random rand = new Random();
        double targetTime = 1f / 30f;
        double timeAcc = 0f;
        double oldTime = glfwGetTime();
        while( !glfwWindowShouldClose(window) ) {
//            glClearColor(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 0.0f);
            double currTime = glfwGetTime();
            timeAcc += currTime - oldTime;
            oldTime = currTime;

            if(timeAcc >= targetTime) {
                timeAcc -= targetTime;

                modelMatrix.rotate((float) (1f * targetTime), 1.0f, 0.3f, 0f);
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    FloatBuffer model = modelMatrix
//                    .rotate(45, 1.0f, 1.0f, 0f)
                            .get(stack.mallocFloat(4 * 4));
                    glUniformMatrix4fv(uniformModelHandle, false, model);
                }
            }

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

//            glDrawArrays(GL_TRIANGLES, 0, vertexCount);
            glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_INT, 0);

            glfwSwapBuffers(window);

            glfwPollEvents();
        }

        glDeleteVertexArrays(vaoHandle);
    }

    private void cleanup() {
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public static void main(String[] args) {
        new Main().run();
    }
}
