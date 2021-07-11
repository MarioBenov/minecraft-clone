import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {
//    private final String vertexShaderSource = "#version 150 core\n" +
//            "        \n" +
//            "in vec3 position;\n" +
//            "in vec3 color;\n" +
//            "\n" +
//            "out vec3 vertexColor;\n" +
//            "\n" +
//            "uniform mat4 model;\n" +
//            "uniform mat4 view;\n" +
//            "uniform mat4 projection;\n" +
//            "\n" +
//            "void main() {\n" +
//            "    vertexColor = color;\n" +
//           // "    vertexColor = vec3(0.1, 0.24, 0.15);\n" +
//            "    mat4 mvp = projection * view * model;\n" +
//            "    gl_Position = mvp * vec4(position, 1.0);\n" +
//            "}";
//    private final String fragmentShaderSource = "#version 150 core\n" +
//            "\n" +
//            "in vec3 vertexColor;\n" +
//            "\n" +
//            "out vec4 fragColor;\n" +
//            "\n" +
//            "void main() {\n" +
//            "    fragColor = vec4(vertexColor, 1.0);\n" +
//            "}";

    private void run() throws IOException {
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
        window = glfwCreateWindow(vidMode.width(), vidMode.height(), "Minecraft clone", NULL, NULL);
        if(window == NULL)
            throw new IllegalStateException("failed to create window");

        glfwSetWindowPos(window, 1920 + 20, 20);

        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        glfwMakeContextCurrent(window);
        glfwShowWindow(window);
    }

    private Camera camera;

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

    private void mainLoop() throws IOException {
        camera = new Camera(window, 1920f / 1080f);

        List<Block> blocks = new ArrayList<>(3);

        Block b1 = new Block();
        Block b2 = new Block();
        Block b3 = new Block();

        b1.getTransform().translate(1f, 0.5f, 0f);
        b2.getTransform().translate(2f, 0.5f, 0f);
        b3.getTransform().translate(1f, 1.5f, 0f);

        blocks.add(b1);
        blocks.add(b2);
        blocks.add(b3);

        List<Line> lines = new ArrayList<>(3);

        Line l1 = new Line(new Vector3f(1f, 0f, 0f), new Vector3f(1f, 0f, 0f));
        Line l2 = new Line(new Vector3f(0f, 1f, 0f), new Vector3f(0f, 1f, 0f));
        Line l3 = new Line(new Vector3f(0f, 0f, 1f), new Vector3f(0f, 0f, 1f));

//        b1.getTransform().translate(0.5f, 0.5f, 0f);
//        b2.getTransform().translate(1.5f, 0.5f, 0f);
//        b3.getTransform().translate(0.5f, 1.5f, 0f);

        lines.add(l1);
        lines.add(l2);
        lines.add(l3);


        String vertexShaderSource = new String(
                Files.readAllBytes(
                        Paths.get(
                                getClass().getResource("shaders/basic.vert").getFile().substring(1)
                        )
                )
        );
        String fragmentShaderSource = new String(Files.readAllBytes(Paths.get(getClass().getResource("shaders/basic.frag").getFile().substring(1))));


        int vertexShaderHandle = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShaderHandle, vertexShaderSource);
        glCompileShader(vertexShaderHandle);

        int status = glGetShaderi(vertexShaderHandle, GL_COMPILE_STATUS);
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

        for(Block b : blocks) {
            b.initShader(shaderProgramHandle);
        }

        for(Line l : lines) {
            l.initShader(shaderProgramHandle);
        }

        int uniformModelHandle = glGetUniformLocation(shaderProgramHandle, "model");
//        Matrix4f modelMatrix  = new Matrix4f();
//        FloatBuffer modelBuffer;
//        try (MemoryStack stack = MemoryStack.stackPush()) {
//            modelBuffer = b2.getTransform()
////                    .rotate(45, 1.0f, 1.0f, 0f)
//                    .get(stack.mallocFloat(4 * 4));
//            glUniformMatrix4fv(uniformModelHandle, false, modelBuffer);
//        }

        int uniformViewHandle = glGetUniformLocation(shaderProgramHandle, "view");
//        Matrix4f viewMatrix  = new Matrix4f().translate(1f, 0f, 0f);
//        FloatBuffer viewBuffer;
//        try (MemoryStack stack = MemoryStack.stackPush()) {
//            viewBuffer = viewMatrix
//                    .get(stack.mallocFloat(4 * 4));
//            glUniformMatrix4fv(uniformViewHandle, false, viewBuffer);
//        }

        int uniformProjectionHandle = glGetUniformLocation(shaderProgramHandle, "projection");
//        FloatBuffer cameraBuffer;
//        try (MemoryStack stack = MemoryStack.stackPush()) {
//            cameraBuffer = camera.getTransform().get(stack.mallocFloat(4 * 4));
////            float aspectRatio = 1920f / 1080f;
////            cameraBuffer = new Matrix4f()
//////                    .ortho(-aspectRatio, aspectRatio, -1f, 1f, -1f, 1f)
////                    .perspective((float) Math.toRadians(95.0f), aspectRatio, 0.01f, 100.0f)
////                    .translate(0f, 0f, -2f)
////                    .get(stack.mallocFloat(4 * 4));
//            glUniformMatrix4fv(uniformProjectionHandle, false, cameraBuffer);
//        }


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

                camera.update(targetTime);

                try (MemoryStack stack = MemoryStack.stackPush()) {
//                    modelBuffer = b2.getTransform().get(stack.mallocFloat(4 * 4));
//                    viewBuffer = viewMatrix.get(stack.mallocFloat(4 * 4));
                    FloatBuffer cameraTransform = camera.getTransform().get(stack.mallocFloat(4 * 4));
                    FloatBuffer cameraProjection = camera.getProjection().get(stack.mallocFloat(4 * 4));

//                    glUniformMatrix4fv(uniformModelHandle, false, modelBuffer);
                    glUniformMatrix4fv(uniformViewHandle, false, cameraTransform);
                    glUniformMatrix4fv(uniformProjectionHandle, false, cameraProjection);
                }
            }

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

//            glDrawArrays(GL_TRIANGLES, 0, vertexCount);
            for(Block b : blocks) {
                b.bind();
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    FloatBuffer modelBuffer = b.getTransform().get(stack.mallocFloat(4 * 4));
                    glUniformMatrix4fv(uniformModelHandle, false, modelBuffer);
                }
                glDrawElements(GL_TRIANGLES, Block.indicesCount, GL_UNSIGNED_INT, 0);
                b.unbind();
            }

            for(Line l : lines) {
                l.bind();
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    FloatBuffer modelBuffer = l.getTransform().get(stack.mallocFloat(4 * 4));
                    glUniformMatrix4fv(uniformModelHandle, false, modelBuffer);
                }
//                glDrawElements(GL_TRIANGLES, Block.indicesCount, GL_UNSIGNED_INT, 0);
                glDrawArrays(GL_LINES, 0, Line.vertexCount);
                l.unbind();
            }

            glfwSwapBuffers(window);

            glfwPollEvents();
        }

//        glDeleteVertexArrays(vaoHandle);
    }

    private void cleanup() {
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public static void main(String[] args) {
        try {
            new Main().run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
