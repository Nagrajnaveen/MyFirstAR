/**
 * MainActivity for MyFirstAR app.
 *
 * This app demonstrates how to use ARCore and Sceneform to render a 3D model in an AR environment.
 * It allows the user to tap on a detected plane to place a 3D model, and provides interaction
 * instructions for scaling and rotation of the model.
 */
package com.nags.myfirstar

import android.app.Activity
import android.app.ActivityManager
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import java.util.Objects
import java.util.function.Consumer
import java.util.function.Function

class MainActivity : AppCompatActivity() {
    // ArFragment instance to handle AR interactions
    private var arCam: ArFragment? = null

    // Counter to track the number of taps
    private var clickNo = 0

    /**
     * Checks whether the device supports the necessary OpenGL version for AR.
     * @param activity The current activity.
     * @return True if the device supports AR, false otherwise.
     */
    private fun checkSystemSupport(activity: Activity): Boolean {
        val openGlVersion =
            (Objects.requireNonNull(activity.getSystemService(ACTIVITY_SERVICE)) as ActivityManager).deviceConfigurationInfo.glEsVersion

        // Check if OpenGL version is greater than or equal to 3.0
        return if (openGlVersion.toDouble() >= 3.0) {
            true
        } else {
            // Display a toast message and finish the activity if AR is not supported
            Toast.makeText(
                activity,
                "App needs OpenGL Version 3.0 or later",
                Toast.LENGTH_SHORT
            ).show()
            activity.finish()
            false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Display an initial toast message for the user
        Toast.makeText(applicationContext, "Project your phone towards any surface", Toast.LENGTH_LONG).show()

        // Check if the device supports AR
        if (checkSystemSupport(this)) {
            // Get the ArFragment from the layout
            arCam = supportFragmentManager.findFragmentById(R.id.arCameraArea) as ArFragment?

            // Set a tap listener for AR plane detection
            arCam!!.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane?, motionEvent: MotionEvent? ->
                clickNo++
                // Display instructions and add 3D model on the first tap
                if (clickNo == 1) {
                    Toast.makeText(applicationContext, "Pinch-out to zoom-in and pinch-in to zoom-out", Toast.LENGTH_LONG).show()
                    val anchor = hitResult.createAnchor()
                    // Build the 3D model and add it to the scene
                    ModelRenderable.builder()
                        .setSource(this, R.raw.apollo_lunar_module)
                        .setIsFilamentGltf(true)
                        .build()
                        .thenAccept(Consumer<ModelRenderable> { modelRenderable: ModelRenderable? ->
                            if (modelRenderable != null) {
                                addModel(
                                    anchor,
                                    modelRenderable
                                )
                            }
                        })
                        .exceptionally(Function<Throwable, Void?> { throwable: Throwable ->
                            // Display an alert if there's an error in rendering the model
                            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                            builder.setMessage("Something is not right" + throwable.message).show()
                            null
                        })
                }
            }
        } else {
            return
        }
    }

    /**
     * Adds a 3D model to the AR scene.
     * @param anchor The anchor where the model will be placed.
     * @param modelRenderable The 3D model renderable.
     */
    private fun addModel(anchor: Anchor, modelRenderable: ModelRenderable) {
        // Create an AnchorNode and attach it to the ArFragment's scene
        val anchorNode = AnchorNode(anchor)
        anchorNode.setParent(arCam!!.arSceneView.scene)

        // Create a TransformableNode and attach the 3D model to it
        val model = TransformableNode(arCam!!.transformationSystem)
        model.scaleController.maxScale = 0.25f
        model.scaleController.minScale = 0.05f
        model.localRotation = Quaternion.axisAngle(Vector3(0f, 0f, 1f), 180f)
        model.setParent(anchorNode)
        model.renderable = modelRenderable
        model.select()
    }
}
