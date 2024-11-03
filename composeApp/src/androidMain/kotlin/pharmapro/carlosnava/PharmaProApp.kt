package pharmapro.carlosnava

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.MobileAds.initialize

class PharmaProApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicializa el SDK de AdMob al iniciar la app
        initialize(this) {}
    }
}
