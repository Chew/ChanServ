require 'discordrb'
require 'yaml'

CONFIG = YAML.load_file('config.yaml')

Bot = Discordrb::Commands::CommandBot.new token: CONFIG['token'],
                                          client_id: CONFIG['client_id'],
                                          prefix: CONFIG['prefix'],
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

Bot.user_ban do |event|
  cases = File.readlines('cases.txt') { |line| line.split.map(&:to_s).join }
  message = Bot.channel(210_174_983_278_690_304).send_message [
    "**Ban** | Case ##{cases.length}",
    "User: #{event.user.name}##{event.user.discrim} (#{event.user.mention})",
    'Reason: Responsible staff please add reason by `;reason case# [reason]`',
    'Responsible staff: [unknown]'
  ].join("\n")
  filename = 'cases.txt'
  File.open(filename, 'a+') { |f| f.puts(message.id.to_s) }
end

Bot.user_unban do |event|
  cases = File.readlines('cases.txt') { |line| line.split.map(&:to_s).join }
  message = Bot.channel(210_174_983_278_690_304).send_message [
    "**Un-ban** | Case ##{cases.length}",
    "User: #{event.user.name}##{event.user.discrim} (#{event.user.mention})",
    'Reason: Responsible staff please add reason by `;reason case# [reason]`',
    'Responsible staff: [unknown]'
  ].join("\n")
  filename = 'cases.txt'
  File.open(filename, 'a+') { |f| f.puts(message.id.to_s) }
end

Bot.member_join do |event|
  Bot.channel(134_445_052_805_120_001).send_embed do |embed|
    embed.title = 'User Joined the Server!'
    embed.colour = 0xd084
    embed.description = "Please welcome #{event.user.mention} to the server!"

    embed.footer = Discordrb::Webhooks::EmbedFooter.new(text: "Member Count: #{event.server.member_count}")
  end
end

Bot.member_leave do |event|
  Bot.channel(134_445_052_805_120_001).send_embed do |embed|
    embed.title = 'User Left the Server!'
    embed.colour = 0xd084
    embed.description = "#{event.user.distinct} left! RIP :("

    embed.footer = Discordrb::Webhooks::EmbedFooter.new(text: "Member Count: #{event.server.member_count}")
  end
end

Bot.message(includes: 'discord.gg') do |event|
  next if %w[Oper Owner Admin Op].include? role(event).to_s
  message = event.message.to_s
  if message.include?('discord.gg')
    event.message.delete
    event.send_temporary_message("#{event.user.mention}, discord link postings are disabled!", 10)
  end
end

puts 'Bot is ready!'
Bot.run
