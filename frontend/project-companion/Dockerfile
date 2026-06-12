# Stage 1: Build the React App
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Stage 2: Serve with NGINX over plain HTTP Only
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html

RUN echo 'server { \
    listen 80; \
    \
    location / { \
        root /usr/share/nginx/html; \
        index index.html index.htm; \
        try_files $uri $uri/ /index.html; \
    } \
    \
    location /api { \
        # ─── REVERTED TO HTTPS ─── \
        proxy_pass https://api.codegenai.online; \
        \
        # (Optional) Add this ONLY if you have self-signed cert issues on the backend \
        # proxy_ssl_verify off; \
        \
        # Stream Extensions for WebFlux SSE \
        proxy_read_timeout 3600s; \
        proxy_send_timeout 3600s; \
        proxy_connect_timeout 75s; \
        proxy_buffering off; \
        proxy_cache off; \
        \
        proxy_http_version 1.1; \
        proxy_set_header Upgrade $http_upgrade; \
        proxy_set_header Connection "upgrade"; \
        proxy_set_header Host api.codegenai.online; \
        proxy_set_header X-Real-IP $remote_addr; \
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for; \
        proxy_set_header X-Forwarded-Proto https; \
    } \
}' > /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]