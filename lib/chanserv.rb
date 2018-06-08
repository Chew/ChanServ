require 'discordrb'

Bot = Discordrb::Commands::CommandBot.new token: 'MjcxNzUwMDg4MzgzMTM1NzQ1.DfQ1aA.cisFrJX3Kj6a9fcuAHcf3m98VD4', client_id: 271_750_088_383_135_745, prefix: ';', help_command: false

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

puts 'Bot is ready!'
Bot.run
