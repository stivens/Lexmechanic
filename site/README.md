# Lexmechanic website

The Jekyll source of https://stivens.github.io/Lexmechanic/. Pushing changes under `site/` to `main` deploys it via
`.github/workflows/pages.yml`.

## Preview locally

Jekyll 4 needs Ruby 3, newer than the one macOS ships. With Docker, from the repository root:

```bash
docker run --rm -it -v "$PWD/site:/srv" -w /srv -p 4000:4000 ruby:3.4 \
  sh -c 'bundle install && bundle exec jekyll serve --host 0.0.0.0'
```

Or with Ruby 3 installed (`brew install ruby`), in `site/`: `bundle install && bundle exec jekyll serve`.

Then open http://localhost:4000/Lexmechanic/.

## Content

- `_data/examples.yml` feeds the examples on the landing page and the gallery. It was extracted from
  `examples/README.md`; add a scene in both places.
- `assets/img` and `assets/video` hold web-sized copies of `art/` and `examples/`, made with:

```bash
cwebp -q 80 -resize 1600 0 examples/input/chapter-0-navigator-1.png -o site/assets/img/chapter-0-navigator-1.webp
cwebp -q 72 -resize 688 0 art/lexmechanic3.jpg -o site/assets/img/lexmechanic3.webp
# Dialogue crops lose the character portraits on both sides, so the text shows larger. Sources differ in
# resolution, so they are scaled to 2402 wide before cropping.
ffmpeg -i examples/input/dialog-crop/chapter-0-navigator-1-dialog-crop.png -vf "scale=2402:-2,crop=1870:ih:270:0" \
  -c:v libwebp -quality 82 site/assets/img/chapter-0-navigator-1-dialog-crop.webp
ffmpeg -ss 3.5 -to 31.5 -i examples/how-it-works.mp4 -c:v libvpx-vp9 -b:v 0 -crf 40 -row-mt 1 -an \
  site/assets/video/how-it-works-loop.webm
ffmpeg -ss 3.5 -to 31.5 -i examples/how-it-works.mp4 -c:v libx264 -preset slow -crf 27 \
  -pix_fmt yuv420p -movflags +faststart -an site/assets/video/how-it-works-loop.mp4
ffmpeg -ss 24 -i examples/how-it-works.mp4 -frames:v 1 -c:v libwebp -quality 75 site/assets/img/poster.webp
ffmpeg -ss 24 -i examples/how-it-works.mp4 -frames:v 1 -vf "scale=1200:-2,crop=1200:630:0:(ih-630)/2" -q:v 3 \
  site/assets/img/og.jpg
```
