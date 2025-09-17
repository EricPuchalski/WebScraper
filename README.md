# 🛒 Proyecto Microservicios - Scraping de Productos Informáticos

Este proyecto implementa una arquitectura de **microservicios en Spring Boot** para realizar scraping de productos informáticos de diferentes tiendas online.  
Los usuarios pueden **navegar los productos**, **guardarlos en favoritos** y recibir **notificaciones por email** cuando los precios bajan.

---

## ⚙️ Microservicios

### 1) Scraper
- Encargado de **extraer datos** de productos de diferentes tiendas.
- Corre como **worker** (scheduler o por eventos).
- **Output:** envía precios encontrados al **Scraper API** / .

---

### 2) Scraper API
- Expone los productos y ofertas scrapeadas a través de una API REST.
- **Responsabilidades:**
  - Normalizar productos de distintas tiendas.
  - Guardar historial de precios.
  - Detectar cambios o descuentos.
 
---

### 3) Gateway
- **Punto único de entrada** a la plataforma.
- Basado en **Spring Cloud Gateway**.
- Funciones:
  - Ruteo a los microservicios.
  - Seguridad (validación de JWT, Token Relay).
  - CORS, rate limiting, logging.

---

### 4) Notification
- Se encarga de **enviar correos electrónicos** a los usuarios.
- 
- Maneja:
  - Plantillas de email.
  - Retries y outbox para asegurar entrega.
  - Integración con SMTP.

---

### 5) Auth Service
- Servicio de autenticación y autorización (**Authorization Server**).
- Emite **tokens JWT** 
- Permite login con usuarios propios o integración con Google.
