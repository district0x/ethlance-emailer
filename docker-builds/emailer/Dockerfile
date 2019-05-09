FROM node:11-stretch-slim
MAINTAINER "Filip Bielejec" <filip@district0x.io>

# ENV variables
ENV WEB3_URL "https://mainnet.district0x.io"

# get compiled JS
COPY emailer /ethlance/emailer
COPY node_modules /ethlance/node_modules

WORKDIR ethlance

ENTRYPOINT ["node", "emailer/ethlance-emailer.js"] 
CMD ["--max-old-space-size=2048"]
