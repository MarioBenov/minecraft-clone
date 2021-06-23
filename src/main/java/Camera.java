import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector3f;

import java.util.Set;
import java.util.TreeSet;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class Camera {
    private final float aspectRatio;
    private Vector3f position = new Vector3f();
    private Vector3f rotation = new Vector3f();

//    private Matrix4f transform = new Matrix4f();

    private Set<Integer> pressedKeys = new TreeSet<>();
    private Vector2d cursorPos = new Vector2d();

    public Matrix4f getTransform() {
        return new Matrix4f()
                .identity()
                .perspective((float) Math.toRadians(95.0f), aspectRatio, 0.01f, 100.0f)
                .rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0))
                .translate(position);
    }

    public Camera(long window, float aspectRatio) {
        this.aspectRatio = aspectRatio;

        glfwSetCursorPosCallback(window, (long _window, double x, double y) -> {
            Vector2d newPos = new Vector2d(x, y);
            Vector2d offset = cursorPos.sub(newPos);

            cursorPos = newPos;

            rotation.y -= 0.025f * offset.x;
            rotation.x -= 0.025f * offset.y;
        });

        glfwSetKeyCallback(window, (long _window, int key, int scancode, int action, int mods) -> {
            if(action == GLFW_PRESS) {
                pressedKeys.add(key);
            } else if(action == GLFW_RELEASE) {
                pressedKeys.remove(key);
            }
        });

        position.z = -5;
    }

    public void update(double deltaTime) {
        Vector3f cameraDir = new Vector3f();
        if(pressedKeys.contains(GLFW_KEY_D)) cameraDir.x = -2f;
        if(pressedKeys.contains(GLFW_KEY_A)) cameraDir.x = 2f;

        if(pressedKeys.contains(GLFW_KEY_S)) cameraDir.z = -2f;
        if(pressedKeys.contains(GLFW_KEY_W)) cameraDir.z = 2f;

        if(pressedKeys.contains(GLFW_KEY_Q)) cameraDir.y = -2f;
        if(pressedKeys.contains(GLFW_KEY_E)) cameraDir.y = 2f;

        position.add(cameraDir.mul((float) deltaTime));
    }
}
