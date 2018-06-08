require 'discordrb'
require 'yaml'

CONFIG = YAML.load_file('config.yaml')

Bot = Discordrb::Commands::CommandBot.new token: CONFIG['token'],
                                          client_id: CONFIG['client_id'],
                                          prefix: ';',
                                          help_command: false

Dir["#{File.dirname(__FILE__)}/plugins/*.rb"].each { |file| require file }

Dir["#{File.dirname(__FILE__)}/plugins/*.rb"].each do |wow|
  bob = File.readlines(wow) { |line| line.split.map(&:to_s).join }
  command = bob[0][7..bob[0].length]
  command.delete!("\n")
  command = Object.const_get(command)
  Bot.include! command
  puts "Plugin #{command} successfully loaded!"
end

puts 'Done loading plugins! Finalizing start-up'

def role(event)
  return 'Oper' if event.user.role?(event.server.roles.find { |role| role.name == 'Oper' }) == true
  return 'Owner' if event.user.role?(event.server.roles.find { |role| role.name == 'Owner' }) == true
  return 'Admin' if event.user.role?(event.server.roles.find { |role| role.name == 'Admins' }) == true
  return 'Op' if event.user.role?(event.server.roles.find { |role| role.name == 'Ops' }) == true
  return 'Half-Op' if event.user.role?(event.server.roles.find { |role| role.name == 'Half-Ops' }) == true
  return 'Voiced' if event.user.role?(event.server.roles.find { |role| role.name == 'Voiced' }) == true
  'Member'
end

def modes(event)
  modes = []
  modes[modes.length] = 'B' if event.user.role?(event.server.roles.find { |role| role.name == '+B' }) == true
  modes[modes.length] = 'Q' if event.user.role?(event.server.roles.find { |role| role.name == '+Q' }) == true
  modes[modes.length] = 'k' if event.user.role?(event.server.roles.find { |role| role.name == '+k' }) == true
  modes[modes.length] = 'd' if event.user.role?(event.server.roles.find { |role| role.name == '+d' }) == true
  modes[modes.length] = 'm' if event.user.role?(event.server.roles.find { |role| role.name == '+m' }) == true
  modes[modes.length] = 'e' if event.user.role?(event.server.roles.find { |role| role.name == '+e' }) == true
  modes
end

puts 'Bot is ready!'
Bot.run
