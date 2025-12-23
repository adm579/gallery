# Samsung Gallery Clone - Aplicación de Galería Android

Una aplicación de galería para Android que replica el diseño y funcionalidad de Samsung Gallery con estilo One UI.

## 🚀 Características

- **Diseño One UI**: Interfaz idéntica a Samsung Gallery
- **Arquitectura MVVM**: Código limpio y mantenible
- **Material 3**: Componentes modernos de Material Design
- **Soporte completo de permisos**: Compatible con Android 13+
- **Modo claro/oscuro**: Automático según configuración del sistema
- **Navegación fluida**: Animaciones suaves y transiciones
- **Visor multimedia**: Zoom, pan, reproducción de videos

## 📱 Pantallas

1. **Pantalla Principal**: Grid de álbumes con miniaturas
2. **Vista de Álbum**: Grid de fotos/videos del álbum seleccionado
3. **Visor Multimedia**: Pantalla completa con controles

## 🛠️ Tecnologías

- **Lenguaje**: Kotlin 100%
- **Arquitectura**: MVVM + ViewBinding
- **UI**: Material 3 + One UI Style
- **Imágenes**: Glide para carga eficiente
- **Videos**: ExoPlayer para reproducción
- **Zoom**: PhotoView para imágenes
- **Mínimo API**: 26 (Android 8.0)

## 📂 Estructura del Proyecto

```
app/
└── src/main/
    ├── java/com/example/samsunggalleryclone/
    │   ├── ui/
    │   │   ├── main/
    │   │   │   └── MainActivity.kt
    │   │   ├── album/
    │   │   │   └── AlbumActivity.kt
    │   │   └── viewer/
    │   │       ├── ViewerActivity.kt
    │   │       └── MediaViewerFragment.kt
    │   ├── adapters/
    │   │   ├── AlbumAdapter.kt
    │   │   ├── MediaAdapter.kt
    │   │   └── MediaViewerAdapter.kt
    │   ├── models/
    │   │   ├── Album.kt
    │   │   └── MediaItem.kt
    │   ├── viewmodel/
    │   │   ├── MainViewModel.kt
    │   │   └── AlbumViewModel.kt
    │   └── utils/
    │       ├── PermissionUtils.kt
    │       └── MediaScanner.kt
    ├── res/
    │   ├── layout/
    │   ├── drawable/
    │   ├── values/
    │   └── values-night/
    └── AndroidManifest.xml
```

## 🔧 Instalación y Configuración

### Requisitos Previos

- Android Studio Hedgehog | 2023.1.1 o superior
- JDK 8 o superior
- Android SDK API 34
- Dispositivo/Emulador con Android 8.0+ (API 26)

### Pasos de Instalación

1. **Clonar/Descargar el proyecto**
   ```bash
   # Si tienes el código en un repositorio
   git clone [URL_DEL_REPOSITORIO]
   
   # O simplemente descargar y extraer los archivos
   ```

2. **Abrir en Android Studio**
   - Abrir Android Studio
   - Seleccionar "Open an Existing Project"
   - Navegar a la carpeta `samsung_gallery_clone`
   - Hacer clic en "OK"

3. **Sincronizar el proyecto**
   - Android Studio automáticamente detectará el proyecto Gradle
   - Hacer clic en "Sync Now" cuando aparezca la notificación
   - Esperar a que se descarguen todas las dependencias

4. **Configurar dispositivo**
   - Conectar un dispositivo Android físico con depuración USB habilitada
   - O crear un emulador Android (recomendado: API 30+ con Google Play)

5. **Compilar y ejecutar**
   - Hacer clic en el botón "Run" (▶️) en Android Studio
   - O usar el atajo de teclado: `Ctrl+R` (Windows/Linux) o `Cmd+R` (Mac)

### Generar APK

Para generar un APK instalable:

1. En Android Studio: `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
2. El APK se generará en: `app/build/outputs/apk/debug/app-debug.apk`

## 📋 Permisos

La aplicación solicita automáticamente los siguientes permisos:

- **Android 13+**: `READ_MEDIA_IMAGES`, `READ_MEDIA_VIDEO`
- **Android 12 y menor**: `READ_EXTERNAL_STORAGE`

## 🎨 Características de Diseño One UI

- **Encabezados grandes**: Tipografía prominente
- **Espaciado amplio**: Layout respirado y cómodo
- **Bordes redondeados**: Cards y elementos con esquinas suaves
- **Iconografía minimalista**: Iconos limpios y reconocibles
- **Animaciones suaves**: Transiciones fluidas entre pantallas
- **Modo oscuro**: Soporte automático según configuración del sistema

## 🔍 Funcionalidades Implementadas

### Pantalla Principal
- ✅ Grid de álbumes (2 columnas)
- ✅ Detección automática de álbumes especiales (Cámara, Screenshots, WhatsApp, etc.)
- ✅ Contador de archivos por álbum
- ✅ Miniaturas de portada
- ✅ Manejo de permisos con explicación

### Vista de Álbum
- ✅ Grid adaptable (3 columnas)
- ✅ Orden por fecha (más reciente primero)
- ✅ Indicadores de video con duración
- ✅ Carga eficiente con Glide
- ✅ Navegación hacia atrás

### Visor Multimedia
- ✅ Pantalla completa inmersiva
- ✅ Navegación swipe izquierda/derecha
- ✅ Zoom con doble tap y pinch (imágenes)
- ✅ Reproducción de videos con ExoPlayer
- ✅ Controles: información, compartir, eliminar
- ✅ UI que se oculta/muestra al tocar

## 🐛 Solución de Problemas

### Error de compilación
- Verificar que Android Studio esté actualizado
- Limpiar proyecto: `Build` → `Clean Project`
- Reconstruir: `Build` → `Rebuild Project`

### Problemas de permisos
- Verificar que el dispositivo tenga fotos/videos
- Conceder permisos manualmente en Configuración → Aplicaciones

### APK no instala
- Habilitar "Fuentes desconocidas" en configuración del dispositivo
- Verificar que el dispositivo tenga Android 8.0+

## 📝 Notas Técnicas

- **ViewBinding**: Habilitado para acceso seguro a vistas
- **Corrutinas**: Para operaciones asíncronas
- **LiveData**: Para observación reactiva de datos
- **Material 3**: Para componentes UI modernos
- **Glide**: Para carga optimizada de imágenes
- **ExoPlayer**: Para reproducción de videos

## 🚀 Próximas Mejoras

- Implementar eliminación real de archivos
- Agregar funcionalidad de selección múltiple
- Implementar búsqueda de archivos
- Agregar soporte para más formatos de archivo
- Implementar edición básica de imágenes

---

**Desarrollado con ❤️ siguiendo las mejores prácticas de Android y el estilo One UI de Samsung**
