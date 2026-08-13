FROM node:20-alpine

WORKDIR /app

# Install better-sqlite3 build dependencies
RUN apk add --no-cache python3 make g++

# Copy server dependencies first (for layer caching)
COPY web/server/package.json ./
RUN npm install --omit=dev

# Copy server source
COPY web/server/ ./

# Copy frontend HTML — server references path.join(__dirname, '..', 'kampuskart.html')
COPY web/kampuskart.html /kampuskart.html

# Copy uploads directory — server references path.join(__dirname, '..', 'uploads')
COPY web/uploads/ /uploads/

# Create data directory for persistent SQLite volume
RUN mkdir -p /data

EXPOSE 3001

CMD ["node", "server.js"]
