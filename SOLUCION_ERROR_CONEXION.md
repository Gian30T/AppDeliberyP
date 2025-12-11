# 🔧 Solución: Error de Conexión en Búsqueda

## Verificaciones Rápidas

### 1. ¿Estás usando emulador o dispositivo físico?

**Si usas EMULADOR Android:**
- La URL está correcta: `http://10.0.2.2:3000/api/`
- Asegúrate que el emulador esté corriendo
- Verifica que la API esté corriendo en tu PC

**Si usas DISPOSITIVO FÍSICO:**
- Necesitas cambiar la URL a la IP de tu computadora
- Sigue los pasos en la sección "Configurar para Dispositivo Físico"

---

## Paso 1: Verificar que la API esté corriendo

En tu terminal, deberías ver:
```
✅ Conexión a MySQL establecida correctamente
🚀 AppDelivery API
Servidor corriendo en: http://localhost:3000
```

Si no está corriendo, ejecuta:
```bash
cd AppDelivery_API
npm start
```

---

## Paso 2: Probar la API directamente

Abre tu navegador y prueba:
```
http://localhost:3000/api/productos/buscar?q=pizza
```

**Deberías ver un JSON con productos.** Si funciona aquí pero no en la app, el problema es la conexión de red.

---

## Paso 3: Configurar para Dispositivo Físico

Si usas un dispositivo físico, necesitas cambiar la URL:

### Opción A: Cambiar en el código (temporal)

En `ApiClient.java`, cambia la línea 28:

```java
// Para emulador:
private static final String BASE_URL = "http://10.0.2.2:3000/api/";

// Para dispositivo físico (reemplaza TU_IP con tu IP):
private static final String BASE_URL = "http://192.168.1.XXX:3000/api/";
```

### Opción B: Obtener tu IP

**Windows:**
```powershell
ipconfig
```
Busca "IPv4 Address" en tu adaptador WiFi o Ethernet.

**Mac/Linux:**
```bash
ifconfig
# o
ip addr show
```

### Importante:
- Tu PC y tu dispositivo Android deben estar en la misma red WiFi
- Desactiva el firewall temporalmente o permite conexiones en el puerto 3000
- La API debe estar corriendo en tu PC

---

## Paso 4: Verificar Logs de la App

1. Abre Android Studio
2. Ve a la pestaña "Logcat" (abajo)
3. Filtra por "SearchActivity"
4. Busca algo en la app
5. Revisa los logs:
   - `Buscando: pizza` - ✅ La búsqueda se inició
   - `Request URL: http://...` - ✅ Se creó la request
   - `Error de conexión: ...` - ❌ Aquí verás el error específico

---

## Errores Comunes y Soluciones

### Error: "Unable to resolve host 10.0.2.2"
- **Causa:** Estás usando dispositivo físico con URL de emulador
- **Solución:** Cambia a la IP de tu PC (ver Paso 3)

### Error: "Connection refused" o "Connection timeout"
- **Causa:** La API no está corriendo o el firewall la bloquea
- **Solución:** 
  1. Verifica que la API esté corriendo (Paso 1)
  2. Desactiva el firewall temporalmente
  3. Verifica que el puerto 3000 esté abierto

### Error: "Network is unreachable"
- **Causa:** El dispositivo y la PC no están en la misma red
- **Solución:** Conecta ambos a la misma WiFi

### No aparece ningún error, solo "Error de conexión"
- **Causa:** Error genérico de Retrofit
- **Solución:** Revisa Logcat para ver el error real (Paso 4)

---

## Solución Rápida: Probar con Emulador

Si tienes problemas con dispositivo físico, prueba primero con emulador:

1. Abre Android Studio
2. Crea un emulador (AVD Manager)
3. Ejecuta la app en el emulador
4. La URL `10.0.2.2:3000` debería funcionar automáticamente

---

## Verificar que Todo Funciona

1. ✅ API corriendo en `localhost:3000`
2. ✅ Productos en la base de datos
3. ✅ API responde en el navegador: `http://localhost:3000/api/productos/buscar?q=pizza`
4. ✅ App Android configurada con la URL correcta (emulador o dispositivo)
5. ✅ Misma red WiFi (si es dispositivo físico)
6. ✅ Firewall desactivado o puerto 3000 abierto

Si todo esto está OK y aún no funciona, comparte los logs de Logcat para diagnosticar más.




