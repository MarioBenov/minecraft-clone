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

    private Matrix4f transform = new Matrix4f();
    private Matrix4f projection;

    private Set<Integer> pressedKeys = new TreeSet<>();
    private Vector2d cursorPos = new Vector2d();

    public Matrix4f getTransform() {
        return transform;
    }

    public Matrix4f getProjection() {
        return projection;
    }

    //    public Matrix4f getTransform() {
//        return new Matrix4f()
//                .identity()
//                .perspective((float) Math.toRadians(95.0f), aspectRatio, 0.01f, 100.0f)
//                .rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
//                .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0))
//                .translate(position);
//    }

    public Camera(long window, float aspectRatio) {
        this.aspectRatio = aspectRatio;

        projection = new Matrix4f()
                .perspective((float) Math.toRadians(95.0f), aspectRatio, 0.01f, 100.0f);

        glfwSetCursorPosCallback(window, (long _window, double x, double y) -> {
            Vector2d newPos = new Vector2d(x, y);
            Vector2d offset = cursorPos.sub(newPos);

            cursorPos = newPos;

//            rotation.y -= 0.025f * offset.x;
//            rotation.x -= 0.025f * offset.y;

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

//        position.z = -5;
        transform.translate(0f, 0f, -5f);
    }

    public void update(double deltaTime) {
//        Vector3f cameraDir = new Vector3f();
//        if(pressedKeys.contains(GLFW_KEY_D)) cameraDir.x = -2f;
//        if(pressedKeys.contains(GLFW_KEY_A)) cameraDir.x = 2f;
//
//        if(pressedKeys.contains(GLFW_KEY_S)) cameraDir.z = -2f;
//        if(pressedKeys.contains(GLFW_KEY_W)) cameraDir.z = 2f;
//
//        if(pressedKeys.contains(GLFW_KEY_Q)) cameraDir.y = -2f;
//        if(pressedKeys.contains(GLFW_KEY_E)) cameraDir.y = 2f;
//
//        position.add(cameraDir.mul((float) deltaTime));

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
