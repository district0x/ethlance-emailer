FROM node:11.14.0-stretch-slim
MAINTAINER "Filip Bielejec" <filip@district0x.io>

# ENV variables
ENV CONFIG /configs/ethlance.config.edn

# get compiled JS
COPY server /emailer/server
COPY resources /emailer/resources
COPY node_modules /emailer/node_modules

WORKDIR emailer

ENTRYPOINT ["node", "server/emailer.js"]
CMD ["--max-old-space-size=2048"]
