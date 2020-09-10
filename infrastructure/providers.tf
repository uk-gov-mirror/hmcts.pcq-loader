provider "azurerm" {
  features {}
  alias           = "mgmt"
  subscription_id = var.mgmt_subscription_id
}
