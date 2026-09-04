# --- Build stage ---
FROM node:20-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
# Baked in at build time since Vite inlines VITE_* vars into the static
# bundle -- override with --build-arg if the backend lives somewhere other
# than the docker-compose default.
ARG VITE_API_BASE_URL=http://localhost:8082
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL
RUN npm run build

# --- Serve stage ---
FROM nginx:1.27-alpine
COPY --from=build /app/dist /usr/share/nginx/html
# Client-side routing (react-router): let nginx fall back to index.html for
# any path it doesn't recognize as a static file, instead of 404ing routes
# like /add-course on a hard refresh.
RUN printf 'server {\n\
    listen 80;\n\
    root /usr/share/nginx/html;\n\
    index index.html;\n\
    location / {\n\
        try_files $uri /index.html;\n\
    }\n\
}\n' > /etc/nginx/conf.d/default.conf
EXPOSE 80
