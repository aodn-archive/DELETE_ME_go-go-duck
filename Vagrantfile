# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|

  config.vm.box = ENV['VAGRANT_BOX'] || "precise64-chef-client-omnibus-11.4.0-0.4"
  config.vm.box_url = ENV['VAGRANT_BOX_URL'] || "http://binary.aodn.org.au/static/boxes/precise64-chef-client-omnibus-11.4.0-0.4.box"

  config.vm.network "forwarded_port", guest: 25, host: 1025

  $script = <<SCRIPT
ln -s /vagrant/jobs jobs || true
SCRIPT

  config.vm.provision "shell", inline: $script

  config.vm.provision "chef_solo" do |chef|
    chef.add_recipe "imos_core::nco"
    chef.add_recipe "postfix"

    chef.json = {
      'postfix' => {
        'main' => {
          'inet_interfaces' => 'all',
          'mynetworks' => '127.0.0.0/8, 10.0.2.0/24'
        }
      }
    }
  end
end
