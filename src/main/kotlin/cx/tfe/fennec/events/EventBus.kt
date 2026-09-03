package cx.tfe.fennec.events

import com.google.common.eventbus.Subscribe
import java.lang.reflect.Method
import java.util.concurrent.CopyOnWriteArrayList

object EventBus {
    // Maps Event classes to a list of registered objects and their listening methods
    private val listeners = mutableMapOf<Class<*>, CopyOnWriteArrayList<EventTarget>>()

    private data class EventTarget(val instance: Any, val method: Method)

    fun register(subscriber: Any) {
        // declaredMethods only returns methods declared directly on a class,
        // not ones inherited from a superclass (e.g. GuiMacroModule's
        // @Subscribe methods as seen from an AutoEnchanting/AutoUbiks
        // instance) — so walk the hierarchy instead of just the leaf class.
        var klass: Class<*>? = subscriber.javaClass
        val seen = mutableSetOf<String>()
        while (klass != null && klass != Any::class.java) {
            for (method in klass.declaredMethods) {
                // Guard against double-registering a method that's visible
                // at multiple levels (e.g. an override).
                val signature = method.name + method.parameterTypes.joinToString(",")
                if (!seen.add(signature)) continue

                // Only register methods with our annotation and exactly one parameter (the event)
                if (method.isAnnotationPresent(Subscribe::class.java) && method.parameterCount == 1) {
                    val eventType = method.parameterTypes[0]
                    method.isAccessible = true

                    val list = listeners.getOrPut(eventType) { CopyOnWriteArrayList() }
                    list.add(EventTarget(subscriber, method))
                }
            }
            klass = klass.superclass
        }
    }

    fun unregister(subscriber: Any) {
        listeners.values.forEach { list ->
            list.removeIf { it.instance == subscriber }
        }
    }

    fun post(event: Any) {
        val eventType = event.javaClass
        listeners[eventType]?.forEach { target ->
            try {
                target.method.invoke(target.instance, event)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}