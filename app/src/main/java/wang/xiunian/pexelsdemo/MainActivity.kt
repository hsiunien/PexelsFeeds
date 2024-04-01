package wang.xiunian.pexelsdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tencent.mmkv.MMKV
import wang.xiunian.pexelsdemo.ui.main.MainFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
            MMKV.initialize(this)

        }
    }

//    object p : Thread.UncaughtExceptionHandler {
//        override fun uncaughtException(p0: Thread, p1: Throwable) {
//            println(p1)
//        }
//    }
}