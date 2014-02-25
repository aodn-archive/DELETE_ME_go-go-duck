site :opscode

["chef-solo-encrypted-data-bags", "imos_core", "postfix"].each do |cookbook_name|
  cookbook cookbook_name, github: 'aodn/chef', rel: "cookbooks/#{cookbook_name}", protocol: :ssh
end
