import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {
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

//        Init objects
        List<Block> blocks = new ArrayList<>(3);

        Block b1 = new Block();
        Block b2 = new Block();
        Block b3 = new Block();
        Block b4 = new Block();
        Block b5 = new Block();
        Block b6 = new Block();

        b1.getTransform().translate(1f, 0.5f, 0f);
        b2.getTransform().translate(2f, 0.5f, 0f);
        b3.getTransform().translate(1f, 1.5f, 0f);
        b4.getTransform().translate(-5f, -1f, 2f);
        b5.getTransform().translate(-3f, 2f, -4f);
        b6.getTransform().translate(-6f, 9f, 0f);

        blocks.add(b1);
        blocks.add(b2);
        blocks.add(b3);
        blocks.add(b4);
        blocks.add(b5);
        blocks.add(b6);

        List<Line> lines = new ArrayList<>(3);

        Line l1 = new Line(new Vector3f(1f, 0f, 0f), new Vector3f(1f, 0f, 0f));
        Line l2 = new Line(new Vector3f(0f, 1f, 0f), new Vector3f(0f, 1f, 0f));
        Line l3 = new Line(new Vector3f(0f, 0f, 1f), new Vector3f(0f, 0f, 1f));

        lines.add(l1);
        lines.add(l2);
        lines.add(l3);

//        Init shaders
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

//        Shader handles
        int uniformModelHandle = glGetUniformLocation(shaderProgramHandle, "model");
        int uniformViewHandle = glGetUniformLocation(shaderProgramHandle, "view");
        int uniformProjectionHandle = glGetUniformLocation(shaderProgramHandle, "projection");

        int uniformLightPos = glGetUniformLocation(shaderProgramHandle, "lightPos");
        int uniformViewPos = glGetUniformLocation(shaderProgramHandle, "viewPos");
        int uniformAmbientColor = glGetUniformLocation(shaderProgramHandle, "ambientColor");
        int uniformAmbientStrength = glGetUniformLocation(shaderProgramHandle, "ambientStrength");
        int uniformDiffuseColor = glGetUniformLocation(shaderProgramHandle, "diffuseColor");
        int uniformDiffuseStrength = glGetUniformLocation(shaderProgramHandle, "diffuseStrength");
        int uniformSpecularColor = glGetUniformLocation(shaderProgramHandle, "specularColor");
        int uniformSpecularStrength = glGetUniformLocation(shaderProgramHandle, "specularStrength");
        int uniformSpecularShininess = glGetUniformLocation(shaderProgramHandle, "specularShininess");

//        Light params
        Vector3f lightPos = new Vector3f(1f, 8f, 5f);
        Vector3f ambientColor = new Vector3f(0.92f,0.61f,0.14f);
        float ambientStrength = 0.35f;
        Vector3f diffuseColor = new Vector3f(0.94f, 0.76f, 0.28f);
        float diffuseStrength = 0.65f;
        Vector3f specularColor = new Vector3f(0.14f, 0.80f, 0.97f);
        float specularStrength = 0.75f;
        int specularShininess = 32;

        glUniform3f(uniformAmbientColor, ambientColor.x, ambientColor.y, ambientColor.z);
        glUniform3f(uniformDiffuseColor, diffuseColor.x, diffuseColor.y, diffuseColor.z);
        glUniform3f(uniformSpecularColor, specularColor.x, specularColor.y, specularColor.z);

        glUniform1f(uniformAmbientStrength, ambientStrength);
        glUniform1f(uniformDiffuseStrength, diffuseStrength);
        glUniform1f(uniformSpecularStrength, specularStrength);

        glUniform1i(uniformSpecularShininess, specularShininess);

//        Timing
        double targetTime = 1f / 30f;
        double timeAcc = 0f;
        double oldTime = glfwGetTime();

//        Main loop
        while( !glfwWindowShouldClose(window) ) {
            double currTime = glfwGetTime();
            timeAcc += currTime - oldTime;
            oldTime = currTime;

            if(timeAcc >= targetTime) {
                timeAcc -= targetTime;

                camera.update(targetTime);

                try (MemoryStack stack = MemoryStack.stackPush()) {
                    FloatBuffer cameraTransform = camera.getTransform().get(stack.mallocFloat(4 * 4));
                    FloatBuffer cameraProjection = camera.getProjection().get(stack.mallocFloat(4 * 4));

                    glUniformMatrix4fv(uniformViewHandle, false, cameraTransform);
                    glUniformMatrix4fv(uniformProjectionHandle, false, cameraProjection);
                }

                lightPos.rotateY((float) (1 * targetTime));
                glUniform3f(uniformLightPos, lightPos.x, lightPos.y, lightPos.z);
            }

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Vector3f camPos = new Vector3f();
            camera.getTransform().getTranslation(camPos);
            glUniform3f(uniformViewPos, camPos.x, camPos.y, camPos.z);

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
                glDrawArrays(GL_LINES, 0, Line.vertexCount);
                l.unbind();
            }

            glfwSwapBuffers(window);

            glfwPollEvents();
        }
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
