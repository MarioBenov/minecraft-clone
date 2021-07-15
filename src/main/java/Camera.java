import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector3f;

import java.util.Set;
import java.util.TreeSet;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class Camera {
    private final Matrix4f transform = new Matrix4f();
    private final Matrix4f projection;

    private final Set<Integer> pressedKeys = new TreeSet<>();
    private Vector2d cursorPos = new Vector2d();

    public Matrix4f getTransform() {
        return transform;
    }

    public Matrix4f getProjection() {
        return projection;
    }

    public Camera(long window, float aspectRatio) {
        projection = new Matrix4f()
                .perspective((float) Math.toRadians(95.0f), aspectRatio, 0.01f, 100.0f);

        glfwSetCursorPosCallback(window, (long _window, double x, double y) -> {
            Vector2d newPos = new Vector2d(x, y);
            Vector2d offset = cursorPos.sub(newPos);

            cursorPos = newPos;

            transform.rotateLocalY((float) (-0.0025f * offset.x));
            transform.rotateLocalX((float) (-0.0025f * offset.y));
            transform.withLookAtUp(0f, 1f, 0f);
        });

        glfwSetKeyCallback(window, (long _window, int key, int scancode, int action, int mods) -> {
            if(action == GLFW_PRESS) {
                pressedKeys.add(key);
            } else if(action == GLFW_RELEASE) {
                pressedKeys.remove(key);
            }
        });

        transform.translate(0f, 0f, -5f);
    }

    public void update(double deltaTime) {
        Vector3f forward = new Vector3f();
        Vector3f right = new Vector3f();
        Vector3f up = new Vector3f();

        if(pressedKeys.contains(GLFW_KEY_A)) transform.positiveX(right);
        if(pressedKeys.contains(GLFW_KEY_D)) transform.positiveX(right).negate();

        if(pressedKeys.contains(GLFW_KEY_W)) transform.positiveZ(forward);
        if(pressedKeys.contains(GLFW_KEY_S)) transform.positiveZ(forward).negate();

        if(pressedKeys.contains(GLFW_KEY_Q)) transform.positiveY(up);
        if(pressedKeys.contains(GLFW_KEY_E)) transform.positiveY(up).negate();

        transform
                .translate(forward.mul(2f).mul((float)deltaTime))
                .translate(right.mul(2f).mul((float)deltaTime))
                .translate(up.mul(2f).mul((float)deltaTime));
    }
}
