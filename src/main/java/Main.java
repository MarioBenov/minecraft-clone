import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {
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
        // ...

        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
//        PointerBuffer monitors = glfwGetMonitors();
        window = glfwCreateWindow(vidMode.width(), vidMode.height(), "Minecraft clone", NULL, NULL);
        if(window == NULL)
            throw new IllegalStateException("failed to create window");


        glfwMakeContextCurrent(window);
        glfwShowWindow(window);
    }

    private void initGl() {
        GL.createCapabilities();

        glClearColor(0.35f, 0.35f, 0.35f, 0.0f);
    }

    private void init() {
        initWindow();
        initGl();
    }

    private void mainLoop() {
        Random rand = new Random();
        while( !glfwWindowShouldClose(window) ) {
            glClearColor(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 0.0f);
            glClear(GL_COLOR_BUFFER_BIT);
            glfwSwapBuffers(window);

            glfwPollEvents();
        }
    }

    private void cleanup() {
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public static void main(String[] args) {
        new Main().run();
    }
}
