# GitHub Actions CI/CD Workflows

This directory contains GitHub Actions workflows for the Ticketing Platform microservices.

## Workflows

### 1. Build & Test (`build.yml`)
- **Trigger**: Push/PR to service directories
- **Purpose**: Build and test all microservices independently
- **Matrix Strategy**: Runs for each service (api-gateway, auth-service, booking-service, discovery-service, event-service, ticket-service)
- **Steps**:
  - Checkout code
  - Setup JDK 21 with Maven caching
  - Compile code
  - Run unit tests
  - Package JAR files
  - Upload build artifacts

### 2. Docker Build & Push (`docker.yml`)
- **Trigger**: After successful build workflow completion or manual dispatch
- **Purpose**: Build Docker images and push to registry
- **Matrix Strategy**: Builds images for each service
- **Steps**:
  - Checkout code
  - Setup Docker Buildx
  - Login to Docker Hub
  - Extract metadata (tags, labels)
  - Build and push images with caching
  - Generate build provenance attestation

### 3. Deploy (`deploy.yml`)
- **Trigger**: Manual workflow dispatch
- **Purpose**: Deploy to Kubernetes environments via Helm
- **Inputs**:
  - `environment`: dev/staging/prod
  - `service`: (optional) specific service to deploy
- **Steps**:
  - Setup Helm and kubectl
  - Configure Kubernetes context
  - Deploy via Helm with environment-specific values
  - Verify deployment and run smoke tests

## Required Secrets

Set these in your GitHub repository settings:

- `DOCKERHUB_USERNAME`: Your Docker Hub username
- `DOCKERHUB_TOKEN`: Docker Hub access token
- `KUBE_CONFIG`: Base64 encoded Kubernetes config for cluster access

## Environments

The repository uses GitHub environments for deployment protection:

- **dev**: Development environment (auto-deploy)
- **staging**: Staging environment (requires approval)
- **prod**: Production environment (requires approval)

## Usage

1. **Automatic CI**: Push changes to any service directory to trigger build/test
2. **Manual Deploy**: Go to Actions → Deploy → Run workflow → Select environment
3. **Specific Service Deploy**: Use the `service` input to deploy only one service

## Branch Protection

Consider setting up branch protection rules:
- Require status checks to pass
- Require up-to-date branches
- Include administrators in restrictions