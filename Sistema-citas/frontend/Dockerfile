# ==========================================
# ETAPA 1: Construcción (Build)
# ==========================================
FROM node:20-alpine AS build

WORKDIR /app

# Copiamos los archivos de dependencias
COPY package*.json ./

# Instalamos las dependencias del proyecto React
RUN npm install

# Copiamos el resto del código fuente del frontend
COPY . .

# Generamos el bundle de producción optimizado en el directorio dist/
RUN npm run build

# ==========================================
# ETAPA 2: Servidor Web de Producción (Serve)
# ==========================================
FROM nginx:alpine

# ¿Por qué Nginx es mucho más eficiente para producción que "vite preview" o "npm run dev"?
# - "npm run dev" o "vite preview" son servidores de desarrollo diseñados para recarga en caliente (HMR) y depuración, 
#   lo cual consume recursos innecesarios de Node.js en producción.
# - Nginx es un servidor web ultrarrápido y extremadamente ligero optimizado para servir archivos estáticos masivos 
#   con un consumo mínimo de memoria y CPU, ideal para entornos de producción y contenedores.

# Copiamos los archivos estáticos compilados desde la etapa de build al directorio web predeterminado de Nginx
COPY --from=build /app/dist /usr/share/nginx/html

# Sobrescribimos la configuración por defecto de Nginx con nuestro archivo personalizado para soportar SPA routing
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Exponemos el puerto 80 del servidor Nginx interno
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
