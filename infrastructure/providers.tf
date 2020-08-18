provider "azurerm" {
  version = "=1.44.0"
}

provider "azurerm" {
  alias           = "mgmt"
  subscription_id = "${var.mgmt_subscription_id}"
}
