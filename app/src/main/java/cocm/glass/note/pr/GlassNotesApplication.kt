package cocm.glass.note.pr

import android.app.Application
import cocm.glass.note.pr.di.AppContainer
import cocm.glass.note.pr.di.AppContainerImpl

class GlassNotesApplication : Application() {
    
    lateinit var container: AppContainer
    
    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}
