require 'discordrb'
require 'yaml'

CONFIG = YAML.load_file('config.yaml')

Bot = Discordrb::Commands::CommandBot.new token: CONFIG['token'],
                                          client_id: CONFIG['client_id'],
                                          prefix: CONFIG['prefix'],
                                          help_command: false

Dir["#{File.dirname(__FILE__)}/plugins/*.rb"].sort.each do |wow|
  require wow
  load wow
  bob = File.readlines(wow) { |line| line.split.map(&:to_s).join }
  command = bob[0][7..bob[0].length]
  command.delete!("\n")
  command = Object.const_get(command)
  Bot.include! command
  puts "Plugin #{command} successfully loaded!"
end

puts 'Done loading plugins! Finalizing start-up'

ROLES = {
  "+k" => 420725473602174996,
  "Oper" => 708085586489245757,
  "NetAdmin" => 424381316688117760,
  "Owner" => 708085624514543728,
  "Admins" => 428372679356055574,
  "Ops" => 319161007010480129,
  "Half-Ops" => 319160664822120451,
  "Voiced" => 708088239310897263,
  "+B" => 363809388697354253,
  "+Q" => 575490034061279239,
  "+d" => 423987790854881290,
  "+e" => 424364607893930005,
  "+m" => 424008605902045185,
  "+r" => 478028312472453122,
  "+w" => 437705588064124948
}.freeze

def role(member, _)
  return 'Member' if member.roles.count.zero?
  return 'Oper' if member.role?(ROLES['Oper']) == true
  return 'Owner' if member.role?(ROLES['Owner']) == true
  return 'Admin' if member.role?(ROLES['Admins']) == true
  return 'Op' if member.role?(ROLES['Ops']) == true
  return 'Half-Op' if member.role?(ROLES['Half-ops']) == true
  return 'Voiced' if member.role?(ROLES['Voiced']) == true

  'Member'
end

def modes(member, _)
  modes = []
  modes.push('Y') if member.role?(ROLES['Oper']) == true
  modes.push('q') if member.role?(ROLES['Owner']) == true
  modes.push('a') if member.role?(ROLES['Admins']) == true
  modes.push('o') if member.role?(ROLES['Ops']) == true
  modes.push('h') if member.role?(ROLES['Half-ops']) == true
  modes.push('v') if member.role?(ROLES['Voiced']) == true
  modes.push('B') if member.role?(ROLES['+B']) == true
  modes.push('Q') if member.role?(ROLES['+Q']) == true
  modes.push('k') if member.role?(ROLES['+k']) == true
  modes.push('d') if member.role?(ROLES['+d']) == true
  modes.push('m') if member.role?(ROLES['+m']) == true
  modes.push('e') if member.role?(ROLES['+e']) == true
  modes.push('e') if member.role?(ROLES['+w']) == true
  modes.push('e') if member.role?(ROLES['+n']) == true
  modes.push('e') if member.role?(ROLES['+r']) == true
  modes
end

Bot.user_ban do |event|
  cases = File.readlines('cases.txt') { |line| line.split.map(&:to_s).join }
  message = Bot.channel(210_174_983_278_690_304).send_embed do |embed|
    embed.title = "Ban | Case ##{cases.length}"
    embed.colour = 0xd084

    embed.add_field(name: 'User', value: "#{event.user.distinct} (#{event.user.mention})", inline: true)
    embed.add_field(name: 'Responsible Staff', value: '[Unknown]', inline: true)
    embed.add_field(name: 'Reason', value: 'Responsible staff please add reason by `;reason case# [reason]`', inline: true)
  end
  filename = 'cases.txt'
  File.open(filename, 'a+') { |f| f.puts(message.id.to_s) }
end

Bot.user_unban do |event|
  cases = File.readlines('cases.txt') { |line| line.split.map(&:to_s).join }
  message = Bot.channel(210_174_983_278_690_304).send_embed do |embed|
    embed.title = "Un-Ban | Case ##{cases.length}"
    embed.colour = 0xd084

    embed.add_field(name: 'User', value: "#{event.user.distinct} (#{event.user.mention})", inline: true)
    embed.add_field(name: 'Responsible Staff', value: '[Unknown]', inline: true)
    embed.add_field(name: 'Reason', value: 'Responsible staff please add reason by `;reason case# [reason]`', inline: true)
  end
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
  next if event.server.nil?
  next if %w[Oper Owner Admin Op].include? role(event.author, event.server).to_s

  message = event.message.to_s
  if message.include?('discord.gg')
    event.message.delete
    event.send_temporary_message("#{event.user.mention}, discord link postings are disabled!", 10)
  end
end

Bot.message(includes: '') do |event|
  next if event.server.nil?
  next if %w[Oper Owner Admin Op].include? role(event.author, event.server).to_s
  next unless [134_445_052_805_120_001, 235_175_875_732_176_896, 421_472_240_169_910_282].include? event.channel.id.to_i

  message = event.message.to_s
  message.downcase!
  swears = File.readlines('swears.txt') { |line| line.split.map(&:to_s).join }
  swears = swears[0]
  swears.delete!("\n")
  swears = swears.split(',')
  swears.each do |swear|
    next unless message.include?(swear)
    event.message.delete
    event.send_temporary_message("#{event.user.mention}, please do not swear!", 10)
    Bot.channel(424_005_662_113_136_640).send_embed do |e|
      e.title = 'Someone just swore!'

      e.add_field(name: 'Invoker', value: event.user.mention, inline: true)
      e.add_field(name: 'Swear', value: swear, inline: true)
      e.add_field(name: 'Channel', value: "<##{event.channel.id}>", inline: true)
      e.add_field(name: 'Message', value: message, inline: false)

      e.color = 'FF0000'
    end
  end
end

puts 'Bot is ready!'
Bot.run
