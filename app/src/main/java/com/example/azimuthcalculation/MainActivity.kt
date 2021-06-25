package com.example.azimuthcalculation


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.PI
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity(), SensorEventListener {
    private var mManager: SensorManager by Delegates.notNull<SensorManager>()
    private var mSensor: Sensor by Delegates.notNull<Sensor>()
    private var mSensor1: Sensor by Delegates.notNull<Sensor>()
    var Accl_list: FloatArray = floatArrayOf(3470F, 3470F, 3470F)
    var magnet_list: FloatArray = floatArrayOf(3470F, 3470F, 3470F)
    var rotation_matrix: FloatArray = floatArrayOf(3470F, 3470F, 3470F, 3470F, 3470F, 3470F, 3470F, 3470F, 3470F)
    var rotation_list: FloatArray = floatArrayOf(3470F, 3470F, 3470F)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //sensor manager
        mManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        //Sensors
        mSensor = mManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mSensor1 = mManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor == mSensor) {
            var index = 0
            for (t in event?.values!!) {
//            Log.v("RawData:",t.toString())
                Accl_list[index] = t
                index = index + 1
            }
        } else if (event?.sensor == mSensor1) {
            var index = 0
            for (t in event?.values!!) {
//            Log.v("RawData:",t.toString())
                magnet_list[index] = t
                index = index + 1
            }
        }
        updateOrientationAngles()
    }

    //Event at accuracy change
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    //Pause when user is away
    override fun onPause() {
        super.onPause()
        //comment out here to listen in background
        mManager.unregisterListener(this)
        StoptCyclicHandler()
    }

    override fun onResume() {
        super.onResume()
        //リスナーとセンサーオブジェクトを渡す
        //第一引数はインターフェースを継承したクラス、今回はthis
        //第二引数は取得したセンサーオブジェクト
        //第三引数は更新頻度 UIはUI表示向き、FASTはできるだけ早く、GAMEはゲーム向き
        mManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI)
        mManager.registerListener(this, mSensor1, SensorManager.SENSOR_DELAY_UI)
    }

    fun test_get(view: View) {
        button.isClickable = false
        StartCyclicHandler()
    }
    fun test_stop(view: View) {
        StoptCyclicHandler()
    }

    private var m_runnable: Runnable? = null
    private val m_handler = Handler()
    protected fun StartCyclicHandler() {
        m_runnable = object : Runnable {
            override fun run() {
                val builderAx = StringBuilder()
                builderAx.append(" x: ").append(Accl_list[0].toString())
                Accx.text = builderAx.toString()
                val builderAy = StringBuilder()
                builderAy.append(" y: ").append(Accl_list[1].toString())
                Accy.text = builderAy.toString()
                val builderAz = StringBuilder()
                builderAz.append(" z: ").append(Accl_list[2].toString())
                Accz.text = builderAz.toString()

                val builderMx = StringBuilder()
                builderMx.append(" x: ").append(magnet_list[0].toString())
                Magx.text = builderMx.toString()
                val builderMy = StringBuilder()
                builderMy.append(" y: ").append(magnet_list[1].toString())
                Magy.text = builderMy.toString()
                val builderMz = StringBuilder()
                builderMz.append(" z: ").append(magnet_list[2].toString())
                Magz.text = builderMz.toString()

                val builderAzim = StringBuilder()
                var azim = rotation_list[0]
                if(azim < 0) {
                    azim = 2 * PI.toFloat() + azim
                }
                azim = 180 * azim / PI.toFloat()
                builderAzim.append(" Azimuth: ").append(azim)//rotation_list[0].toString())
                Azimuth.text = builderAzim.toString()
                val builderPitch = StringBuilder()
                var pitch = -rotation_list[1]
                pitch = 180 * pitch / PI.toFloat()
                builderPitch.append(" _Pitch_: ").append(pitch)//rotation_list[1].toString())
                Pitch.text = builderPitch.toString()
                val builderRoll = StringBuilder()
                var roll = rotation_list[2]
                roll = 180 * roll / PI.toFloat()
                builderRoll.append(" __Roll__: ").append(roll)//rotation_list[2].toString())
                Roll.text = builderRoll.toString()
//                Log.v("Handle :", "test")
                m_handler.postDelayed(this, 250)    // msスリープ
            }
        }
        m_handler.post(m_runnable)     // スレッド起動
    }

    protected fun StoptCyclicHandler() {
        m_handler.removeCallbacks(m_runnable)
        button.isClickable = true
    }

    private fun updateOrientationAngles() {
        SensorManager.getRotationMatrix(rotation_matrix, null, Accl_list, magnet_list)
        SensorManager.getOrientation(rotation_matrix, rotation_list)
//        Log.v("Rot :", rotation_list.toString())
    }
}