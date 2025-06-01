provider "google" {
  project = var.project_id
  region  = var.region
}

module "gke" {
  source  = "terraform-google-modules/kubernetes-engine/google"
  version = "~> 30.0"

  project_id = var.project_id
  name       = "${var.project_slug}-cluster"
  region     = var.region
  network    = "default"

  node_pools = [
    {
      name         = "default-node-pool"
      machine_type = "e2-medium"
      min_count    = 1
      max_count    = 2
      auto_upgrade = true
    }
  ]
}
