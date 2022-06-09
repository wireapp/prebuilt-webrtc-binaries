#! /usr/bin/env python3

import github
import os
import re
import subprocess
import sys

user = 'wireapp'
repo = 'prebuilt-webrtc-binaries'

release = os.environ['WEBRTC_RELEASE']
build  = os.environ['BUILD_NUMBER']
token = os.environ.get('GITHUB_TOKEN')

rb = '{}.{}'.format(release, build)

print('repo: {}/{}'.format(user, repo))
print('build: {}.{}'.format(release, build))

gh = github.Github(token)
grepo = gh.get_user(user).get_repo(repo)

grel = grepo.get_releases()

found_rel = None
for r in grel:
	if r.tag_name == rb:
		found_rel = r
		break

if not found_rel:
	print('release {} not found, tagging and pushing'.format(rb))
	subprocess.call(['git', 'tag', rb])
	subprocess.call(['git', 'push', 'origin', rb])
	found_rel = grepo.create_git_release(rb, rb, 'Version {}'.format(rb))

for fl in os.listdir('.'):
	if re.match('webrtc_{}.*\.zip'.format(release), fl):
		print(fl)
		found_rel.upload_asset(fl)
