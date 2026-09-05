# Stage 1: Build React Vite app
FROM node:20-alpine AS builder
WORKDIR /app

# Install npm dependencies
COPY package*.json ./
RUN npm ci

# Copy source and create production build
COPY . .
RUN npm run build

# Stage 2: Lightweight Nginx web server
FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
