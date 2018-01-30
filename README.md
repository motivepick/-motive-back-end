# Stay Motivated Back End

The service that is going to defeat the laziness. Coming soon...

## How to configure the development instance

### Variables

```bash
export APPLICATIONS=/app
export GITHUB_USER_NAME=yaskovdev
export GITHUB_REPO_NAME=stay-motivated-back-end
```

### Configure the GitHub repository

### Configure Git-Auto-Deploy

[Git-Auto-Deploy](https://github.com/olipo186/Git-Auto-Deploy) is a tool that allows to deploy an application automatically on Git push.

#### Prerequisites

1. Git is installed
2. Git-Auto-Deploy is installed

#### Steps to configure

1. `cd ${APPLICATIONS} && git clone https://github.com/${GITHUB_USER_NAME}/${GITHUB_REPO_NAME}.git`
2. `chown -R git-auto-deploy:git-auto-deploy ${APPLICATIONS}/${GITHUB_REPO_NAME}`
3. Replace settings of Git-Auto-Deploy with the `git-auto-deploy.conf.json` file provided in the current repository by executing: `rm -rf /etc/git-auto-deploy.conf.json && cp ${APPLICATIONS}/${GITHUB_REPO_NAME}/git-auto-deploy.conf.json /etc && chown git-auto-deploy:git-auto-deploy /etc/git-auto-deploy.conf.json`
4. `service git-auto-deploy restart`
