# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|

  config.vm.box = ENV['VAGRANT_BOX'] || "precise64-chef-client-omnibus-11.4.0-0.4"
  config.vm.box_url = ENV['VAGRANT_BOX_URL'] || "http://binary.aodn.org.au/static/boxes/precise64-chef-client-omnibus-11.4.0-0.4.box"

  # TODO: replace with chef recipe when it's available...
  $script = <<SCRIPT
NCO_PACKAGE=nco_4.3.4-1_amd64.deb
wget -O /tmp/${NCO_PACKAGE} https://jenkins.aodn.org.au/job/nco/lastSuccessfulBuild/artifact/resources/worker/${NCO_PACKAGE}
(sudo dpkg -i /tmp/${NCO_PACKAGE}; true)
sudo apt-get -f -y install
sudo apt-get install -y netcdf-bin
SCRIPT

  config.vm.provision "shell", inline: $script

end
